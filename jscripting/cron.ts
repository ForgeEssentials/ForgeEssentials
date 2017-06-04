//
// Primitive Cron FEJscript
//


//
// Stops the server after maxServerUptime milliseconds
//
const maxServerUptime = 28740000;
const stopDelay = '60000';

setInterval(function()
{
    Server.chatConfirm('Server is going to restart in five minutes!');
    setTimeout(function()
    {
        Server.chatConfirm('Server is going to restart in one minute!');
        setTimeout(function()
        {
          stopServer(Server.getServer());
        }, stopDelay);
    }, '300000');
}, maxServerUptime);

function stopServer(sender: mc.ICommandSender)
{
    var hiddenChatSender = sender.doAs(null, true);
    Server.runCommand(hiddenChatSender, 'stop');
}
