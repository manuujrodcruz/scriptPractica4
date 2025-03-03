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

# Reiniciar Apache para aplicar cambios
sudo systemctl restart apache2

# Clonar el repositorio de donde se tomara 

echo "Configurando Virtual Hosts..."



# Habilitar los sitios y reiniciar Apache
sudo a2ensite default-ssl
sudo systemctl restart apache2

# Obtener certificado SSL con Let's Encrypt
sudo certbot --apache -d tu-dominio.com -d www.tu-dominio.com --non-interactive --agree-tos -m tu-email@example.com

# Reiniciar Apache para aplicar el SSL
sudo systemctl restart apache2

echo "Configuración completada. Apache con Virtual Hosts y SSL está funcionando."

