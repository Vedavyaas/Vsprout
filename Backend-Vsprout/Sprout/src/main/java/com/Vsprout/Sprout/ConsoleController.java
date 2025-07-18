package com.Vsprout.Sprout;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ConsoleController {

    @PostMapping("/run")
    public String runCode(@RequestBody String code) {
        try {
            String[] inputs = code.trim().split("(?<=\\.>)");
            StringBuilder output = new StringBuilder();

            for (String line : inputs) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (!line.endsWith(".>")) {
                    return "Error: Each command must end with `.>`";
                }

                String command = line.substring(0, line.length() - 2).trim();

                if (command.startsWith("shout ") || command.startsWith("judge ") || command.startsWith("loop ")) {
                    Executions executions = new Executions(command);
                    String result = executions.main();
                    output.append(result).append("\n");
                } else {
                    output.append("Error : Unknown command\n");
                }
            }

            return output.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Internal Server Error - " + e.getMessage();
        }
    }
}