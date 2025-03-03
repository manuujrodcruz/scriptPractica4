#!/usr/bin/env bash

# Mensaje de inicio
echo "Bienvenido al script de configuracion, le saluda Manuel Rodriguez, disfrute! :D"

set -e  # Detener el script en caso de error

echo "Instalando y configurando Apache con Virtual Host y SSL"

# Actualizar el sistema e instalar dependencias
sudo apt update && sudo apt -y install apache2 certbot python3-certbot-apache unzip zip

# Habilitar módulos necesarios en Apache
sudo a2enmod ssl
sudo a2enmod proxy
sudo a2enmod proxy_http

# Instalando la versión sdkman y java
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Utilizando la versión de java 17 como base.
sdk install java 21.0.3-tem

# Clonar el repositorio de donde se tomara 
git clone https://github.com/manuujrodcruz/scriptPractica4.git

echo "Configurando Virtual Hosts..."

# Copiando archivo de configuracion de SSL en el lugar indicado
sudo cp ~/scriptPractica4/seguro.conf /etc/apache2/sites-available/

# Habilitando
sudo a2ensite seguro.conf

# Verificando y generando certificados SSL
DOMINIOS=("app1.manuelrodriguez.tech" "app2.manuelrodriguez.tech")
for DOMINIO in "${DOMINIOS[@]}"; do
    if [ ! -f "/etc/letsencrypt/live/$DOMINIO/cert.pem" ]; then
        echo "Certificado para $DOMINIO no encontrado. Generando..."
        sudo certbot certonly --standalone -d "$DOMINIO" --non-interactive --agree-tos -m "mrmanueljoserodriguezcruz@gmail.com"
    else
        echo "Certificado para $DOMINIO ya existe."
    fi
done
# Reiniciar Apache para aplicar cambios
sudo systemctl restart apache2

#Moviendo a la carpeta app1
cd ~/scriptPractica4/app1

# Ejecutando la creación de fatjar
chmod +x gradlew
./gradlew shadowjar

# Subiendo la aplicación 1 puerto por defecto.
java -jar ~/scriptPractica4/app1/build/libs/app.jar > ~/scriptPractica4/app1/build/libs/salida.txt 2> ~/scriptPractica4/app1/build/libs/error.txt &

#Moviendo a la carpeta app2
cd ../app2

# Ejecutando la creación de fatjar
chmod +x gradlew
./gradlew shadowjar

# Subiendo la aplicación 2 puerto por defecto.
java -jar ~/scriptPractica4/app2/build/libs/app.jar > ~/scriptPractica4/app2/build/libs/salida.txt 2> ~/scriptPractica4/app2/build/libs/error.txt &

echo "Configuración completada. Apache con Virtual Hosts y SSL está funcionando."

