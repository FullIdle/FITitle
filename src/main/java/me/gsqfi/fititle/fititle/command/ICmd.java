package me.gsqfi.fititle.fititle.command;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class ICmd implements TabExecutor {
    private final ICmd superCmd;
    private final String name;
    private final Map<String,ICmd> subCmdMap = new HashMap<>();

    public ICmd(String name,ICmd superCmd){
        this.name = name;
        this.superCmd = superCmd;
        if (this.superCmd != null) {
            this.superCmd.getSubCmdMap().put(this.name,this);
        }
    }

    public List<String> getSubCmdNames(){
        return new ArrayList<>(this.subCmdMap.keySet());
    }

    public String[] removeOneArg(String[] args){
        ArrayList<String> list = Lists.newArrayList(args);
        if (!list.isEmpty()){
            list.remove(0);
            if (!list.isEmpty() && list.get(0).isEmpty()) list.remove(0);
        }
        return list.toArray(new String[0]);
    }
}
