<?php
	$server = '127.0.0.1';
	$port = 25566;
	$key = file_get_contents('key.key');
	
	require __DIR__ . '/ForgeEssentialsQuery.class.php';
		
	$Timer = MicroTime( true );
	$Query = new ForgeEssentialsQuery();

	try
	{
		$info = $Query->Get($key, $server, $port, $_GET);
	}
	catch( Exception $e )
	{
		$error = $e->getMessage();
	}
?>

<html>
<head>
	<title>Snooper Example</title>
	<link rel="stylesheet" href="http://driesgames.game-server.cc/bootstrap.css">
</head>
<body>
	<header class="page-header">
		<h1>Snooper Demo</h1>
	</header>
	<hr>
	<div class="container">
		<div>
			<p>
				You can change the responce by setting "?is=x" in the url.<br>
				If a responce needs a username or other extra date, add "&username=xxxx" to the url.
			</p>
		</div>
		<div class="span12">
			<?php if( isset( $GLOBALS['error'] ) ): ?>
			<div class="alert alert-info">
				<h4 class="alert-heading">Exception:</h4>
				<?php echo htmlspecialchars( $GLOBALS['error'] ); ?>
			</div>
			<?php else: ?>
			<div>
				<h5>Player info:</h5>
				<?php $GLOBALS['Query']->printTable($GLOBALS['info']); ?>
			</div>
			<div>
				<h5>RAW server info:</h5>
				<pre><?php var_dump($GLOBALS['info']); ?></pre>
			</div>
			<?php endif; ?>
		</div>
	</div>
	<hr>
	<footer>
		<p class="pull-right"><span class="badge badge-info">Generated in <?php echo Number_Format( ( MicroTime( true ) - $Timer ), 4, '.', '' ); ?>s</span></p>
	</footer>
</body>
</html>