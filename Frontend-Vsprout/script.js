function runCode() {
  const codeInput = document.getElementById('code');
  const code = codeInput.value;  // save current editor content

  const output = document.getElementById('consoleOutput');
  output.textContent = "⏳ Running...";

  fetch('http://localhost:8090/run', {
    method: 'POST',
    headers: { 'Content-Type': 'text/plain' },
    body: code
  })
  .then(res => res.text())
  .then(data => {
    output.textContent = data || "✅ Done (no output)";
    // NO clearing or modifying codeInput.value here
  })
  .catch(err => {
    output.textContent = "❌ Error: " + err.message;
    // NO clearing or modifying codeInput.value here either
  });
}
