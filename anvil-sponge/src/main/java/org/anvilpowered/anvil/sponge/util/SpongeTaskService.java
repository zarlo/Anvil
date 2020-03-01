package org.anvilpowered.anvil.sponge.util;

import org.anvilpowered.anvil.api.util.TaskService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class SpongeTaskService implements TaskService {

    @Inject
    protected PluginContainer pluginContainer;

    @Override
    public void stopAll() {
        Sponge.getScheduler().getScheduledTasks(pluginContainer)
            .forEach(Task::cancel);
    }

    @Override
    public void stop(String name) {
        Sponge.getScheduler().getTasksByName(name)
            .forEach(Task::cancel);
    }

    @Override
    public Builder builder() {
        return new SpongeTaskBuilder();
    }

    protected class SpongeTaskBuilder implements Builder {

        private final Task.Builder builder;

        public SpongeTaskBuilder() {
            builder = Task.builder();
        }

        @Override
        public Builder async() {
            builder.async();
            return this;
        }

        @Override
        public Builder delay(Duration duration) {
            builder.delay(duration.getSeconds(), TimeUnit.SECONDS);
            return this;
        }

        @Override
        public Builder delay(int ticks) {
            builder.delayTicks(ticks);
            return this;
        }

        @Override
        public Builder interval(Duration duration) {
            builder.interval(duration.getSeconds(), TimeUnit.SECONDS);
            return this;
        }

        @Override
        public Builder interval(int ticks) {
            builder.intervalTicks(ticks);
            return this;
        }

        @Override
        public Builder startAtUtc(Instant instantUtc) {
            builder.delay(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(),
                instantUtc).getSeconds(), TimeUnit.SECONDS);
            return this;
        }

        @Override
        public Builder executor(Runnable runnable) {
            builder.execute(runnable);
            return this;
        }

        @Override
        public Builder name(String name) {
            builder.name(name);
            return this;
        }

        @Override
        public void submit() {
            builder.submit(pluginContainer);
        }
    }
}
