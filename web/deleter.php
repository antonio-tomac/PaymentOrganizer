<?php

$backendHost = "http://10.11.12.3:8081";

$type = $_REQUEST['type'];
$groupId = $_REQUEST['groupId'];
$id = $_REQUEST['id'];
$method = "DELETE";
if ($type == 'Payment') {
	$url = "$backendHost/groups/$groupId/payments/$id";
} else if ($type == 'Exchange') {
	$url = "$backendHost/groups/$groupId/exchanges/$id";
} else if ($type == 'Expense') {
	$url = "$backendHost/groups/$groupId/expenses/$id";
}

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
$output = curl_exec($ch);       
curl_close($ch);

header("Location: groupDashboard.php?groupId=$groupId");

