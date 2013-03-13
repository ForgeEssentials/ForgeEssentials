<?php
class ForgeEssentialsQuery
{		
	public function printTable($data)
	{
		echo '<table class="table table-bordered table-striped">';
		foreach($data as $key => $value)
		{
			if(is_numeric($key))
			{
				echo '<tr><td>';
				if(Is_Array($value))
				{
					 $this->printTable($value);
				}
				else
				{
					echo $value;
				}
				echo "</td></tr>";
			}
			else
			{
				echo '<tr><td>';
				echo $key;
				echo "</td><td>";
				if(Is_Array($value))
				{
					 $this->printTable($value);
				}
				else
				{
					echo $value;
				}
				echo "</td></tr>";
			}
		}
		echo '</table>';
	}
	
	public function Get($key, $server, $port, $data, $Timeout = 3)
	{
		$socket = @socket_create(AF_INET, SOCK_STREAM, 0);
		if (!$socket)
			throw new Exception('Unable to create AF_INET socket');
		if(!@socket_connect($socket , $server , $port))
			throw new Exception('Unable to connect');
		if(!@socket_send($socket, Pack('c*', $data["id"]), 1, 0x00))
			throw new Exception('Unable to send id');
		$message = Security::encrypt(json_encode($data), $key);
		if(!@socket_send($socket, $message, strlen($message), 0x00))
			throw new Exception('Unable to send extra data');
		if(!@socket_recv($socket, $buf, 1024 * 1024, MSG_WAITALL))
			throw new Exception('Unable to receive data');
		socket_close($socket);
		return json_decode(Security::decrypt($buf, $key), true);
	}
}

class Security 
{
	public static function encrypt($input, $key) 
	{
		$size = mcrypt_get_block_size(MCRYPT_RIJNDAEL_128, MCRYPT_MODE_ECB); 
		$input = Security::pkcs5_pad($input, $size); 
		$td = mcrypt_module_open(MCRYPT_RIJNDAEL_128, '', MCRYPT_MODE_ECB, ''); 
		$iv = mcrypt_create_iv (mcrypt_enc_get_iv_size($td), MCRYPT_RAND); 
		mcrypt_generic_init($td, $key, $iv); 
		$data = mcrypt_generic($td, $input); 
		mcrypt_generic_deinit($td); 
		mcrypt_module_close($td); 
		$data = base64_encode($data); 
		return $data; 
	} 

	private static function pkcs5_pad ($text, $blocksize) 
	{ 
		$pad = $blocksize - (strlen($text) % $blocksize); 
		return $text . str_repeat(chr($pad), $pad); 
	} 

	public static function decrypt($sStr, $sKey) 
	{
		$decrypted= mcrypt_decrypt(
			MCRYPT_RIJNDAEL_128,
			$sKey, 
			base64_decode($sStr), 
			MCRYPT_MODE_ECB
		);
		$dec_s = strlen($decrypted); 
		$padding = ord($decrypted[$dec_s-1]); 
		$decrypted = substr($decrypted, 0, -$padding);
		return $decrypted;
	}	
}

?>