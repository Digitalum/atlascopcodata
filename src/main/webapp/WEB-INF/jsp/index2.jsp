<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Normalization Product Names</title>
    <style>
        label {
            display: block;
            font: 1rem 'Fira Sans', sans-serif;
        }

        input,
        label {
            margin: .4rem 0;
        }

    </style>
</head>
<body>
<a href="keywords">Keywords</a><br/>
<a href="keywords">Sentences</a><br/>
<a href="keywords">Rules</a><br/>
<a href="keywords">Word replacements</a><br/>
<div style="margin: 5% 30%;width: 300px;background-color: #cbefff;padding: 20px;">
    <label for="fileupload">Choose a Excel:</label>
    <input type="file" id="fileupload" name="fileupload"
           accept="application/msexcel,application/vnd.ms-excel,application/x-msexcel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
    <button id="upload-button" onclick="uploadFile()"> Upload</button>
</div>
<div style="margin: 1% 30%;width: 300px;background-color: #efcbff;padding: 20px;">
    <label for="clean-button">Start Cleaning</label>
    <button id="clean-button" onclick="cleanData()"> Clean</button>
</div>
<!-- Ajax JavaScript File Upload to Spring Boot Logic -->
<script>
    async function uploadFile() {
        let formData = new FormData();
        formData.append("file", fileupload.files[0]);
        let response = await fetch('/upload', {
            method: "POST",
            body: formData
        });

        if (response.status == 200) {
            alert("File successfully uploaded.");
        }
    }

    async function cleanData() {
        let response = await fetch('/cleandata', {
            method: "POST"
        });

        if (response.status == 200) {
            alert("Clean data successfully.");
        }
    }
</script>
</body>
</html>