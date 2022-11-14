<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>File manager</title>
    <h1> ${currentTime}</h1>
    <form method="post">
        <input type="submit" name="exitBtn" value="Exit" />
    </form>
    <h1> ${currentPath}</h1>
    <h2>Files</h2>
    <form>${files}</form>
    <h2>Folders</h2>
    <form>${folders}</form>
</head>
<body>
</body>
</html>