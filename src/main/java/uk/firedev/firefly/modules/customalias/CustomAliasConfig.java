package uk.firedev.firefly.modules.customalias;

import uk.firedev.daisylib.Config;
import uk.firedev.daisylib.libs.boostedyaml.block.implementation.Section;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public class CustomAliasConfig extends Config {

    private static CustomAliasConfig instance;

    private CustomAliasConfig() {
        super("modules/customalias.yml", "modules/customalias.yml", Firefly.getInstance(), false);
    }

    public static CustomAliasConfig getInstance() {
        if (instance == null) {
            instance = new CustomAliasConfig();
        }
        return instance;
    }

    public List<CommandBuilder> getCommandBuilders() {
        List<CommandBuilder> list = new ArrayList<>();
        getConfig().getRoutesAsStrings(false).forEach(key -> {
            Section section = getConfig().getSection(key);
            if (section == null) {
                return;
            }
            list.add(new CommandBuilder(section));
        });
        return list;
    }

    public List<String> getCommandBuilderNames() {
        List<String> names = new ArrayList<>();
        for (CommandBuilder builder : getCommandBuilders()) {
            names.add(builder.getCommandName());
            names.addAll(builder.getAliases());
        }
        return names;
    }

}
