You can make your own connector or use the premade ForgeEssentialsQuery.class.php
The index.php is an example of how you should use the ForgeEssentialsQuery.
You need to make sure you include the "key.key"

The get funtion in ForgeEssentialsQuery has a field "data", that should be an array with at least 1 item called "id".
That "id" will determine what response you will get. If a response needs a username to extra data, it will tell you.

Make sure you change the IP and port before you start telling me it doens't work.

~~ Dries007

If you get the following error:
`PHP Fatal error:  Call to undefined function mcrypt_get_block_size() in ForgeEssentialsQuery.class.php`
You need to install mcrypt. On a Debian-based distribution, you do this with:
`sudo apt-get install php5-mcrypt`
