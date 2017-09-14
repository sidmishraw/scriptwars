/**
 * Project: ScriptWars
 * Package: io.sidmishraw.scriptwars.core
 * File: MainDriver.java
 * 
 * @author sidmishraw
 *         Last modified: Sep 11, 2017 12:47:20 PM
 */
package io.sidmishraw.scriptwars.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Random;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.scriptwars.core.MainDriver
 *
 */
public class MainDriver {
	
	// js engine name
	private static final String			NASHORN	= "nashorn";
	
	private static HttpServer			server	= null;
	private static StandardLambdaObject	sObject	= null;
	
	// the agent
	private static Agent				agent	= null;
	
	/**
	 * Creates the serialized object
	 * 
	 * @param jsFilePath
	 *            the file path of the js script file
	 */
	private static void createSerializedObject(String jsFilePath) {
		
		sObject = new StandardLambdaObject();
		
		sObject.setObjectId("Sid#0001");
		
		StringBuffer scriptContentBuffer = new StringBuffer();
		
		try (BufferedReader bReader = new BufferedReader(
		        new InputStreamReader(new FileInputStream(Paths.get(jsFilePath).toFile())))) {
			
			String line = null;
			
			while (null != (line = bReader.readLine())) {
				
				scriptContentBuffer.append(line);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		// set the js script as the new lambda as a transport(?)
		sObject.setLambdaJSScript(scriptContentBuffer.toString());
	}
	
	/**
	 * Evaluates the script
	 * 
	 * @param script
	 *            the script to be executed
	 * @throws ScriptException
	 * 
	 * @return the result of the invocable js engine
	 */
	private static Invocable scriptIt(String script) throws ScriptException {
		
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		
		// list of all registered scripting engines supported by default
		// Oracle Nashhorn for javascript
		// scriptEngineManager.getEngineFactories().forEach(fac -> {
		//
		// System.out.println(fac.getEngineName());
		// System.out.println(fac.getLanguageName());
		// });
		
		ScriptEngine jsEngine = scriptEngineManager.getEngineByName(NASHORN);
		
		jsEngine.eval(script);
		
		return (Invocable) jsEngine;
	}
	
	/**
	 * Server context definition
	 * 
	 * @param jsFilePath
	 *            the js file path
	 */
	private static void addServerContext(String jsFilePath) {
		
		// serializes the object and sends it to the agent at the particular
		// host and port
		// request-body of form "host=hostname&port=port-nbr"
		server.createContext("/serialize", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				System.out.println("Serializing the object");
				
				StringBuffer sBuffer = new StringBuffer();
				
				try (BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()))) {
					
					String line = null;
					
					while (null != (line = br.readLine())) {
						
						sBuffer.append(line);
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				String[] requestBodySplits = sBuffer.toString().split("\\&");
				
				String hostNamePart = URLDecoder.decode(requestBodySplits[0], "UTF-8").split("=")[1];
				String portPart = URLDecoder.decode(requestBodySplits[1], "UTF-8").split("=")[1];
				
				System.out.println("host=" + hostNamePart + " and port=" + portPart);
				
				// create the serializable object and stream it to the passed in
				// agent
				createSerializedObject(jsFilePath);
				
				// send the object to the partner agent by desrializing it
				URL url = new URL("http://" + hostNamePart + ":" + portPart + "/deserialize");
				
				System.out.println("URL to write to :: " + url.toString());
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				
				System.out.println("Object :: " + sObject.toString());
				
				// send the HTTP request
				try (ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream())) {
					
					out.writeObject(sObject);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				System.out.println("Awaiting response");
				
				// get the response from agent
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					
					String decodedString;
					while ((decodedString = in.readLine()) != null) {
						
						System.out.println(decodedString);
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				String response = "Object Serialized";
				
				t.sendResponseHeaders(200, response.length());
				
				OutputStream os = t.getResponseBody();
				
				os.write(response.getBytes());
				
				os.close();
			}
		});
		
		// deserialzes the object from the request stream
		server.createContext("/deserialize", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				System.out.println("Deserializing the object");
				
				try (ObjectInputStream oin = new ObjectInputStream(t.getRequestBody())) {
					
					StandardLambdaObject receivedObject = (StandardLambdaObject) oin.readObject();
					
					// execute the js script
					scriptIt(receivedObject.getLambdaJSScript());
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				String response = "Object deserialized";
				
				t.sendResponseHeaders(200, response.length());
				
				OutputStream os = t.getResponseBody();
				
				os.write(response.toString().getBytes());
				
				os.close();
			}
		});
		
		// attack the opponent
		server.createContext("/attack", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				System.out.println("Computing the attack");
				
				StringBuffer sBuffer = new StringBuffer();
				
				try (BufferedReader br = new BufferedReader(new InputStreamReader(t.getRequestBody()))) {
					
					String line = null;
					
					while (null != (line = br.readLine())) {
						
						sBuffer.append(line);
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				String[] requestBodySplits = sBuffer.toString().split("\\&");
				
				String hostNamePart = URLDecoder.decode(requestBodySplits[0], "UTF-8").split("=")[1];
				String portPart = URLDecoder.decode(requestBodySplits[1], "UTF-8").split("=")[1];
				
				System.out.println("Attacking agent at :: " + hostNamePart + ":" + portPart);
				
				// create the serializable object and stream it to the passed in
				// agent
				createSerializedObject(jsFilePath);
				
				// send the object to the partner agent by desrializing it
				URL url = new URL("http://" + hostNamePart + ":" + portPart + "/receive");
				
				System.out.println("Initiating attack...");
				
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				
				System.out.println("Logging:: Attack object :: " + sObject.toString());
				
				// send the HTTP request
				try (ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream())) {
					
					out.writeObject(sObject);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				System.out.println("Awaiting response...");
				
				// get the response from agent
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					
					String decodedString;
					
					while ((decodedString = in.readLine()) != null) {
						
						System.out.println("Attack stats :: " + decodedString);
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				String response = "Attack turn over!";
				
				t.sendResponseHeaders(200, response.length());
				
				OutputStream os = t.getResponseBody();
				
				os.write(response.getBytes());
				
				os.close();
			}
		});
		
		// receive the attack
		server.createContext("/receive", new HttpHandler() {
			
			@Override
			public void handle(HttpExchange t) throws IOException {
				
				System.out.println("Receiving attack from :: " + t.getRequestURI());
				
				Boolean flag = true;
				Double dmg = 0.0;
				
				try (ObjectInputStream oin = new ObjectInputStream(t.getRequestBody())) {
					
					StandardLambdaObject receivedObject = (StandardLambdaObject) oin.readObject();
					
					// execute the js script
					Invocable engine = scriptIt(receivedObject.getLambdaJSScript());
					
					dmg = (Double) engine.invokeFunction("computeDamage", 100, (new Random()).nextDouble());
					
					final Double damageDealt = dmg;
					
					// received attack
					flag = agent.receiveAttack(() -> damageDealt);
					
					System.out.println("Damage received:: " + dmg);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				System.out.println("Your HP:: " + agent.getHp());
				
				String response = "";
				
				if (flag) {
					
					response = "Attack succeeded, damage dealt :: " + dmg.toString();
				} else {
					
					response = "Attack missed! Try again...";
				}
				
				boolean stopFlag = false;
				
				if (agent.getHp() <= 0) {
					
					response += "\n The agent at :: " + t.getLocalAddress() + " has been defeated, way to go dude!";
					
					stopFlag = true;
				}
				
				t.sendResponseHeaders(200, response.length());
				
				OutputStream os = t.getResponseBody();
				
				os.write(response.toString().getBytes());
				
				os.close();
				
				// stops the server now that you have been defeated! :(
				if (stopFlag) {
					
					server.stop(0);
				}
			}
		});
	}
	
	/**
	 * Creates a HTTPServer and binds it to the provided host and port
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 * 
	 * @param jsFilePath
	 *            the htmlPath /file path to the html file
	 */
	private static void createHttpServer(String host, Integer port, String jsFilePath) {
		
		try {
			
			server = HttpServer.create(new InetSocketAddress(host, port), 0);
			
			addServerContext(jsFilePath);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints the usage
	 */
	private static void printUsage() {
		
		System.out.println("java -jar scriptwar.jar <host> <port> <path to js file>");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length < 3) {
			
			System.err.println("Needs host and port: mandatory and the js file path");
			printUsage();
			System.exit(1);
		}
		
		String host = args[0];
		Integer port = Integer.parseInt(args[1]);
		
		System.out.printf("Host:: %s :: Port :: %d", host, port);
		
		// create the HTTP server
		createHttpServer(host, port, args[2]);
		
		agent = new Agent(host);
		
		server.start();
	}
}
