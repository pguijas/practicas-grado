import socket
import select
import subprocess

HEADER_LENGTH = 10

IP = "127.0.0.1"
PORT = 11234

# inicializar conexión
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((IP, PORT))
server_socket.listen()

# lista de conexiones
sockets_list = [server_socket]

# lista de clientes activos
clients = {}

print(f'Listening for connections on {IP}:{PORT}...')

# función para recibir mensajes
def receive_message(client_socket):
    try:
        # cabecera del mensaje
        message_header = client_socket.recv(HEADER_LENGTH)
        # si no se ha recibido nada pasamos
        if not len(message_header):
            return False
        # cogemos la longitud del mensaje
        message_length = int(message_header.decode('utf-8').strip())
        # devolvemos un dict con la cabecera y los datos recibidos
        return {'header': message_header, 'data': client_socket.recv(message_length)}
    except Exception:
        return False


# ejecutar las acciones del cliente sobre el binario que contiene la gramática
def execute_command(command):
    # conversión de bytes a str y lo pasámos a minúsculas
    data = str(command['data'].decode("utf-8")).lower()
    # guardamos las ordenes del cliente en un fichero
    with open(".cmd","w+") as f:
        f.write(data)
    # le pasamos el archivo al binario y recogemos su output
    stdout,stderr = subprocess.Popen(["./gramm"], stdin=open(".cmd","r"),stdout=subprocess.PIPE, stderr=subprocess.STDOUT).communicate()
    subprocess.Popen(["rm", ".cmd"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    # devolvemos el stderr si hubo algún error
    if stderr is not None:
        #print(stderr.decode("utf-8"))
        return stderr
    # devolvemos el stdout si no ha habido errores
    if stdout is not None:
        #print(stdout.decode("utf-8"))
        return stdout


# bucle principal
while True:
    # esperamos a que un socket quiera conectarse con nosotros, se devuelven como read_sockets
    read_sockets, _, exception_sockets = select.select(sockets_list, [], sockets_list)
    # para cada socket que se quiere conectar:
    for notified_socket in read_sockets:
        # si se conectan a nuestro socket, significa que no se le ha asignado uno, asi que tenemos que entablar una nueva conexión
        if notified_socket == server_socket:
            # aceptamos la conexión
            client_socket, client_address = server_socket.accept()
            # recibimos el mensaje
            user = receive_message(client_socket)
            # si no se ha autenticado no aceptamos la conexión
            if user is False:
                continue
            # añadimos el nuevo cliente a la lista de sockets y clientes
            sockets_list.append(client_socket)
            clients[client_socket] = user
            print('Accepted new connection from {}:{}, username: {}'.format(*client_address, user['data'].decode('utf-8')))
        else:
            # recogemos el mensaje del socket notificado
            message = receive_message(notified_socket)
            # si el mensaje es null, cerramos la conexión
            if message is False:
                print(f"Closed connection from: {clients[notified_socket]['data'].decode('utf-8')}")
                sockets_list.remove(notified_socket)
                del clients[notified_socket]
                continue
            # recogemos el usuario que nos ha mandado el mensaje y lo sacamos por pantalla
            user = clients[notified_socket]
            print(f'Received message from {user["data"].decode("utf-8")}: {message["data"].decode("utf-8")}')
            for client_socket in clients:
                # buscamos al cliente que nos ha mandado ejecutar una orden
                if client_socket == notified_socket:
                    # nos autenticamos
                    username = "server"
                    username_header = f"{len(username):<{HEADER_LENGTH}}".encode('utf-8')
                    # ejecutamos la orden
                    msg = execute_command(message)
                    msg_header = f"{len(msg):<{HEADER_LENGTH}}".encode('utf-8')
                    # le mandamos al cliente el output del programa
                    client_socket.send(username_header + username.encode('utf-8') + msg_header + msg)
            
    for notified_socket in exception_sockets:
        # si un cliente se ha desconectado de manera abrupta lo eliminamos
        sockets_list.remove(notified_socket)
        del clients[notified_socket]
