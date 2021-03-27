/**
 * Represents the WebSocket client and abstracts implementation of WebSockets in connection
 * with the real-time module on the SmartOCR API
 * @author Joel Seidel
 */

export default class client {
  // Connection object - that is, actual WebSocket connection object
  connection;
  // WebSockets socket URL
  wsURL = "ws://localhost:2021";

  /**
   * Constructor for creating a default client ~ creates WebSocket connection and sets default methods
   */
  constructor() {
    // Create WebSocket connection item to specified address
    this.connection = new WebSocket(this.wsURL)
    // Set message handler to private handler method
    this.connection.onmessage = onMessage()
    // Set open handler to private handler method
    this.connection.onopen = onOpen()
  }
}

/**
 * Handler for receiving messages from the WebSockets server
 * @param e event arguments
 */
function onMessage (e) {
  console.log(e)
}

/**
 * Handler for connection opening
 * @param e event arguments
 */
function onOpen (e) {
  console.log("opened")
}
