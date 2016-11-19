package milk.tagmanager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import milk.tagmanager.text.Tag;

import java.util.HashMap;

public class TagManager extends PluginBase implements Listener{

    HashMap<String, String> data = new HashMap<>();

    @Override
    public void onEnable(){
        Entity.registerEntity("Tag", Tag.class, true);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getLogger().info(TextFormat.GOLD + "[TagManager]Plugin has been enabled");
    }

    @Override
    public void onDisable(){
        this.getServer().getLogger().info(TextFormat.GOLD + "[TagManager]Plugin has been disabled");
    }

    @EventHandler
    public void onPlayerBlockEvent(PlayerInteractEvent ev){
        if(ev.getFace() == 255 || ev.getAction() != PlayerInteractEvent.RIGHT_CLICK_BLOCK){
            return;
        }

        Player player = ev.getPlayer();
        if(!data.containsKey(player.getName().toLowerCase())){
            return;
        }
        Tag.create(data.get(player.getName().toLowerCase()), ev.getBlock().add(0.5, 0.5, 0.5));
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent ev){
        Player player =  ev.getPlayer();
    }

    public static void tagItem(EntityItem item, String text){
        long flags = 0;
        flags |= 1 << Entity.DATA_FLAG_IMMOBILE;
        flags |= 1 << Entity.DATA_FLAG_CAN_SHOW_NAMETAG;
        flags |= 1 << Entity.DATA_FLAG_ALWAYS_SHOW_NAMETAG;
        item.setDataProperty(new LongEntityData(Entity.DATA_FLAGS, flags), false);
        item.setDataProperty(new StringEntityData(Entity.DATA_NAMETAG, text), false);

        item.sendData(item.getViewers().values().stream().toArray(Player[]::new));
    }

    @Override
    public boolean onCommand(CommandSender k, Command cmd, String label, String[] args){
        String output = "[TagManager]";
        if(!(k instanceof Player)){
            k.sendMessage(output + "게임에서 입력해주세요");
            return true;
        }

        Player sender = (Player) k;
        switch(args.length){
            case 1:
                output += "텍스트를 띄울곳을 터치해주세요";
                data.put(sender.getName().toLowerCase(), args[0]);
                break;
            case 4:
            case 5:
                Position pos = new Position(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                if(args.length == 5){
                    pos.level = Server.getInstance().getLevelByName(args[4]);
                }
                if(pos.level == null){
                    pos.level = sender.level;
                }
                Tag.create(args[0], pos);
                output += "텍스트를 성공적으로 띄웠어요";
                break;
            default:
                output = cmd.getUsage();
                break;
        }
        sender.sendMessage(output);
        return true;
    }

}
