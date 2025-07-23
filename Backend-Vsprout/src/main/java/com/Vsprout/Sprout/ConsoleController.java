package com.Vsprout.Sprout;

import com.Vsprout.Sprout.Database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class ConsoleController {

    @Autowired
    private Executions executions;
    @Autowired
    private HistoryRepository history;
    @Autowired
    private NotesRepository notes;

    @GetMapping("/notes")
    public List<String> getNotes() {
        List<Notes> dbNote = notes.findAll();
        List<String> inputNotes = new ArrayList<>();
        for (Notes n : dbNote) {
            inputNotes.add(n.getNote());
        }
        return inputNotes;
    }

    @PostMapping(value = "/notes", consumes = "text/plain")
    public List<String> saveNote(@RequestBody String note) {
        notes.save(new Notes(note));
        return getNotes();
    }

    @GetMapping("/history")
    public List<String> getHistory() {
        return history.findAll().stream()
                .map(HistoryEntity::getCode)
                .toList();
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
