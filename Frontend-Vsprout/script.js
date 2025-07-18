function runCode() {
  const code = document.getElementById('code').value;
  const output = document.getElementById('consoleOutput');

  output.textContent = "⏳ Running...";

  fetch('http://localhost:8080/run', {
    method: 'POST',
    headers: {
      'Content-Type': 'text/plain',
    },
    body: code
  })
  .then(res => res.text())
  .then(data => {
    output.textContent = data || "✅ Done (no output)";
  })
  .catch(err => {
    output.textContent = "❌ Error: " + err.message;
  });
}