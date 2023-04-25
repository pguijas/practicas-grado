import socket
import errno
import sys
from time import sleep, time

HEADER_LENGTH = 10

IP = "127.0.0.1"
PORT = 11234
my_username = "client"
client_socket = None

def ext():
    print("Saliendo...")
    sys.exit(0)

# intentamos conectarnos al servidor
try:
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((IP, PORT))
    client_socket.setblocking(False)
    username = my_username.encode('utf-8')
    username_header = f"{len(username):<{HEADER_LENGTH}}".encode('utf-8')
    client_socket.send(username_header + username)
except ConnectionRefusedError as e:
    client_socket = None


try:
    print("Use el comando 'help' para recibir ayuda o 'exit' para salir")
    # bucle principal
    while True:
        # recogemos la orden del usuario
        message = input(f'{my_username} > ')
        if (message=="exit"):
            ext()
        # si no nos pudimos conectar, lo volvemos a intentar durante 30 segundos, antes de salir
        if client_socket is None:
            print("[+] Conection refused, trying to connect...")
            t_end = time() + 30
            # reintento de conexión
            while client_socket is None:
                try:
                    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    client_socket.connect((IP, PORT))
                    client_socket.setblocking(False)
                    username = my_username.encode('utf-8')
                    username_header = f"{len(username):<{HEADER_LENGTH}}".encode('utf-8')
                    client_socket.send(username_header + username)
                except ConnectionRefusedError as e:
                    client_socket = None
                    sleep(2)
                if (time() >= t_end):
                    print("[+] Timeout excedeed, connection refused.")
                    sys.exit(0)
            print("[+] Connected.")

        if message:
            # le mandamos la orden al servidor para que la ejecute
            message = message.encode('utf-8')
            message_header = f"{len(message):<{HEADER_LENGTH}}".encode('utf-8')
            client_socket.send(message_header + message)

            try:
                # variable auxiliar para mantenernos en el bucle en escucha de la respuesta del servidor
                exit = False
                while not exit:
                    try:
                        # intentamos recibir la longitud del nombre del servidor
                        username_header = client_socket.recv(HEADER_LENGTH)
                        # si es null, el servidor está caído
                        if not len(username_header):
                            print('Connection closed by the server')
                            sys.exit()

                        # recibimos los datos de usuario que nos ha mandado el servidor
                        username_length = int(username_header.decode('utf-8').strip())
                        username = client_socket.recv(username_length).decode('utf-8')

                        # recibimos el mensaje
                        message_header = client_socket.recv(HEADER_LENGTH)
                        message_length = int(message_header.decode('utf-8').strip())
                        message = client_socket.recv(message_length).decode('utf-8')
                        # ya podemos salir del bucle
                        exit = True
                        # sacamos por pantalla la respuesta del servidor
                        print(f'{username}:  {message}')
                        
                    except IOError as e:
                        # IOError como error de lectura
                        if e.errno != errno.EAGAIN and e.errno != errno.EWOULDBLOCK:
                            print(f'Reading error: {str(e)}')
                            sys.exit(1)
                        # IOError: no me han mandado nada
                        continue

            except Exception as e:
                print(f'Reading error: {str(e)}')
                sys.exit(1)
except KeyboardInterrupt:
    ext()
