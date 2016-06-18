<?php

$groupsJson = file_get_contents("http://10.11.12.3:8081/groups");
$groups = json_decode($groupsJson);
?>
<html>
	<head>
		<title>Payment organizer - Groups</title>
	</head>
	<body>
		<h2>Group list</h2>
		<ul>
		<?
			foreach ($groups as $group) {
				echo "<li><a href='groupDashboard.php?groupId=".$group->id."'>".$group->name."</a></li>";
			}
		?>
		</ul>
	</body>
</html>


