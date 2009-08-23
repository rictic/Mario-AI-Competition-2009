cd classes;
jar -cf playForever.jar *
cd ..
mv classes/playForever.jar . && jarsigner -keystore ~/.ssh/marioKeyStore -storepass searchThatKoopa playForever.jar marioAI && mv playForever.jar web/