package com.Vsprout.Sprout;

import com.Vsprout.Sprout.Database.HistoryEntity;
import com.Vsprout.Sprout.Database.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ConsoleController {

    @Autowired
    private Executions executions;
    @Autowired
    private HistoryRepository history;

    @GetMapping("/history")
    public List<String> getHistory() {
        List<HistoryEntity> entities = history.findAll();
        List<String> codes = new ArrayList<>();

        for (HistoryEntity entity : entities) {
            codes.add(entity.getCode());
        }

        return codes;
    }

    @PostMapping("/run")
    public String runCode(@RequestBody String code) {
        history.save(new HistoryEntity(code));
        getHistory();
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
