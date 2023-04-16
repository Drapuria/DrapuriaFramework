package net.drapuria.framework.bukkit.protocol.packet;

import net.drapuria.framework.beans.annotation.PreDestroy;
import net.drapuria.framework.beans.annotation.PreInitialize;
import net.drapuria.framework.beans.annotation.Service;
import net.drapuria.framework.bukkit.reflection.minecraft.Minecraft;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.*;

@Service(name = "PacketQueue")
public class PacketSendQueue {

    public static PacketSendQueue INSTANCE;

    private final Queue<QueuedPacket> queue = new LinkedBlockingQueue<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> future = null;

    private long lastPackets = System.currentTimeMillis();

    private boolean initiated = false;

    @PreInitialize
    public void init() {
        INSTANCE = this;
    }

    @PreDestroy
    public void destroy() {
        if (future != null)
            future.cancel(false);
    }

    private void initQueue() {
        if (initiated)
            return;
        initiated = true;
        future = executorService.scheduleAtFixedRate(this::flush, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void add(QueuedPacket packet) {
        this.queue.add(packet);
        initQueue();
    }

    private void flush() {
        if (queue.isEmpty()) return;
        for (int i = 0; i < 10; i++) {
            QueuedPacket packet = queue.poll();
            if (packet == null) {
                if (lastPackets < System.currentTimeMillis() - 300000) {
                    future.cancel(false);
                    future = null;
                    initiated = false;
                }
                return;
            }
            lastPackets = System.currentTimeMillis();
            for (Player player : packet.receiver) {
                if (player != null && player.isOnline())
                    Minecraft.sendPacket(player, packet.packet);
            }
        }
    }

    public static class QueuedPacket {
        private final Player[] receiver;
        private final Packet<?> packet;

        public QueuedPacket(Packet<?> packet, Player... receiver) {
            this.receiver = receiver;
            this.packet = packet;
        }
    }

}
