<?php
//echo "<pre>";
//print_r($_REQUEST);
//echo "</pre>";

$backendHost = "http://10.11.12.3:8081";

$type = $_REQUEST['type'];
$groupId = $_REQUEST['groupId'];
$ammount = urlencode($_REQUEST['ammount']);
$date =  urlencode($_REQUEST['date']);
if ($type == 'Payment') {
	$userId = $_REQUEST['userId'];
	$url = "$backendHost/groups/$groupId/payments";
	$body = json_encode(array(
		'userId' => $userId, 
		'date' => $date, 
		'ammount' => $ammount
	));
	$method = "POST";
} else if ($type == 'Exchange') {
	$fromUserId = $_REQUEST['fromUserId'];
	$toUserId = $_REQUEST['toUserId'];
	$url = "$backendHost/groups/$groupId/exchanges";
	$body = json_encode(array(
		'fromUserId' => $fromUserId, 
		'toUserId' => $toUserId, 
		'date' => $date, 
		'ammount' => $ammount
	));
	$method = "POST";
} else if ($type == 'Expense') {
	$url = "$backendHost/groups/$groupId/expenses";
	$method = "POST";
	$userIds = $_REQUEST['userIds'];
	$name = $_REQUEST['name'];
	$custom = $_REQUEST['distributionType'] == 'custom';
	$equalRatio = 1./count($userIds);
	$userRatios = array();
	foreach ($userIds as $userId) {
		$userRatios[$userId] = $custom ? $_REQUEST["ratio_$userId"] : $equalRatio;
	}
	$body = json_encode(array(
		'name' => $name, 
		'date' => $date, 
		'ammount' => $ammount,
		'userRatios' => $userRatios
	));
} else if ($type == 'Income') {
	$url = "$backendHost/groups/$groupId/incomes";
	$method = "POST";
	$userIds = $_REQUEST['userIds'];
	$name = $_REQUEST['name'];
	$custom = $_REQUEST['distributionType'] == 'custom';
	$equalRatio = 1./count($userIds);
	$userRatios = array();
	foreach ($userIds as $userId) {
		$userRatios[$userId] = $custom ? $_REQUEST["ratio_$userId"] : $equalRatio;
	}
	$body = json_encode(array(
		'name' => $name, 
		'date' => $date, 
		'ammount' => $ammount,
		'userRatios' => $userRatios
	));
} else if ($type == 'Receivement') {
	$userId = $_REQUEST['userId'];
	$url = "$backendHost/groups/$groupId/receivements";
	$body = json_encode(array(
		'userId' => $userId, 
		'date' => $date, 
		'ammount' => $ammount
	));
	$method = "POST";
}

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(                                                                          
    'Content-Type: application/json'                                                                               
)); 
$output = curl_exec($ch);       
curl_close($ch);

//echo "output: $output\n";
//die;
header("Location: groupDashboard.php?groupId=$groupId");

