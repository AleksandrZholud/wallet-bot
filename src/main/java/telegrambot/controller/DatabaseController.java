package telegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import telegrambot.config.multitenancy.TenantManager;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/db-manager")
public class DatabaseController {

    private final TenantManager tenantManager;

    @Transactional
    @GetMapping("/update/{name}")
    public String switchDatabase(@PathVariable String name) {
        long l = System.currentTimeMillis();

        tenantManager.switchDataSource(name, false);

        return "Successfully switched to new database in: " + (System.currentTimeMillis() - l) / 1000.0 + " sec.";
    }
}