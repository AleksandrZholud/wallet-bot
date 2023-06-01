package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import telegrambot.config.multitenancy.TenantManager;
import telegrambot.repository.util.CurrentConditionRepository;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DatabaseController {

    private final TenantManager tenantManager;
    private final CurrentConditionRepository currentConditionRepository;

    @GetMapping("/switch-database/{name}")
    public String switchDatabase(@PathVariable String name) {

        long l = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            long step = System.currentTimeMillis();
            tenantManager.switchDataSource("del_me_1_" + name);
            currentConditionRepository.updateCommand(i + 1L);

            tenantManager.switchDataSource("del_me_2_" + name);
            currentConditionRepository.updateCommand(3L + i);

            log.info("Step in: " + (System.currentTimeMillis() - step) / 1000.0 + " sec.");
        }

        return "Successfully switched to new database in: " + (System.currentTimeMillis() - l) / 1000.0 + " sec.";
    }
}