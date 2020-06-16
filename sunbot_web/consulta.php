  <?php
    header('Content-Type: application/json');
$con = mysqli_connect("localhost", "root", "1234", "dad_sunbot");

if (mysqli_connect_errno($con)) {
    echo "Failed to connect to DataBase: " . mysqli_connect_error();
} else {
    $data_points = array();
    $shum = mysqli_query($con, "SELECT * FROM sensor_value WHERE idsensor=2");
    $slumi = mysqli_query($con, "SELECT * FROM sensor_value WHERE idsensor=1");
    $slumd = mysqli_query($con, "SELECT * FROM sensor_value WHERE idsensor=0");

    while ($row = mysqli_fetch_array($shum)) {
        $dt = new DateTime();
        $dt = $row['timestamp']; //<--- Pass a UNIX TimeStamp
 $point = array("valorxhum" => $dt, "valoryhum" => $row['value']);
 //$point = array("valorxhum" => $row['timestamp'], "valoryhum" => $row['value']);
        array_push($data_points, $point);
    }
    while ($row = mysqli_fetch_array($slumi)) {
        $point = array("valorxlumi" => $row['timestamp'], "valorylumi" => $row['value']);
        array_push($data_points, $point);
    }
    while ($row = mysqli_fetch_array($slumd)) {
        $point = array("valorxlumd" => $row['timestamp'], "valorylumd" => $row['value']);
        array_push($data_points, $point);
      }
    echo json_encode($data_points);
}
mysqli_close($con);
?>
