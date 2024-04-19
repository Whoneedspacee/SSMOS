# SSMOS

SSM Open Source Recreation for community usage

Feel free to fork the code and edit at your leisure 

https://discord.gg/GSnKwyjyU8

# Dependencies (required)	

PandaSpigot

Spigot 1.8.9

# Server Setup Steps

1. Download the Source Code from https://github.com/Whoneedspacee/SSMOS
2. Acquire both a 1.8.8 / 1.8.9 Spigot Jar and Pandaspigot jar
3. Compile the code with both of these as libraries
4. Create a minecraft server as normal using the pandaspigot jar
5. Set allow flight to true in your server properties
6. Set item drop radius to 0 in spigot.yml
7. Turn off Nether in server.properties and End in bukkit.yml for extra performance
8. Set keep spawn chunks loaded to false in paper.yml
9. Set ticks-per autosave in bukkit.yml to 0 or else maps will autosave and lose their points / be griefed
10. Set entity tracking range in spigot.yml to a higher amount, I chose 64 for all types
