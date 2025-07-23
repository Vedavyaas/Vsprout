function runCode(event) {
  if(event) {
    event.preventDefault();
    event.stopPropagation();
  }

  const codeInput = document.getElementById('code');
  const code = codeInput.value;

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
  })
  .catch(err => {
    output.textContent = "❌ Error: " + err.message;
  });
}

window.runCode = runCode;
