#!/bin/bash 

set -e 


PROJECT_DIR="$HOME/Klang" 

cd "$PROJECT_DIR" || { echo "Error: Unable to enter directory $PROJECT_DIR. Aborting."; exit 1; } 

./gradlew :cli:shadowJar

echo "Creating /usr/local/klang ..." 
sudo mkdir -p /usr/local/klang && echo "Created/existing /usr/local/klang directory." || { echo "Failed to create directory."; exit 1; } 

echo "Copying cli/build/libs/k.jar to /usr/local/klang/klang.jar ..." 

sudo cp cli/build/libs/k.jar /usr/local/klang/klang.jar && echo "klang.jar copied successfully." || { echo "Failed to copy klang.jar."; exit 1; } 

echo "Copying k to /usr/local/bin/k" 
sudo cp "$PROJECT_DIR/cli/bin/k" /usr/local/bin/k && echo "k copied to /usr/local/bin/k successfully." || { echo "Failed to copy script k."; exit 1; } 

echo "Granting execution permissions for /usr/local/bin/k..." 
sudo chmod +x /usr/local/bin/k && echo "Permissions adjusted." || { echo "Failed to adjust permissions."; exit 1; } 

echo "Verifying installation by running 'k -V'..." 
if k -V; then 
    echo "Success: The 'k -V' command ran correctly." 
    echo "Installation completed successfully!" 
else 
    echo "Error: The command 'k -V' failed." 
    echo "The installation may not have completed correctly." 
    exit 1 
fi