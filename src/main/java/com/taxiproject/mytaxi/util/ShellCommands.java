package com.taxiproject.mytaxi.util;

import com.taxiproject.mytaxi.service.CarGeneratorService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ShellCommands {

    private final CarGeneratorService carGeneratorService;

    public ShellCommands(CarGeneratorService carGeneratorService) {
        this.carGeneratorService = carGeneratorService;
    }

    @Command(
            name = "generate-cars",
            description = "Создает случайные машины для предприятия",
            group = "MyTaxi",
            help = """
                    Создает N случайных машин для предприятия.
                    Пример: generate-cars -e 1 -c 15
                    """
    )
    public String generateCars(
            @Option(shortName = 'e', longName = "enterpriseId", description = "ID предприятия", required = true)
            UUID enterpriseId,
            @Option(shortName = 'c', longName = "count", description = "Количество машин", defaultValue = "10")
            int count
    ) {
        try {
            carGeneratorService.generateCars(enterpriseId, count);
            return "Успешно создано " + count + " машин для предприятия с ID: " + enterpriseId;
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }
}
