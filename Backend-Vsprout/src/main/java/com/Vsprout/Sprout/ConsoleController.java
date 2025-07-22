package com.Vsprout.Sprout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ConsoleController {

    @Autowired
    private Executions executions;

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
                    executions.setCommand(command);
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
