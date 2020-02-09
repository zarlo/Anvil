/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.nukkit.util;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import org.anvilpowered.anvil.common.util.CommonStringResult;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public class NukkitStringResult extends CommonStringResult<String, CommandSender> {

    @Override
    public Builder<String, CommandSender> builder() {
        return new NukkitStringResultBuilder();
    }

    @Override
    public void send(String result, CommandSender commandSender) {
        commandSender.sendMessage(result);
    }

    @Override
    public String deserialize(String text) {
        return text;
    }

    @Override
    public String serialize(String text) {
        return text;
    }

    private static final class NukkitStringResultBuilder extends CommonStringResultBuilder<String, CommandSender> {
        private final Deque<Object> elements;

        private NukkitStringResultBuilder() {
            elements = new LinkedList<>();
        }

        @Override
        public Builder<String, CommandSender> aqua() {
            elements.add(TextFormat.AQUA);
            return this;
        }

        @Override
        public Builder<String, CommandSender> black() {
            elements.add(TextFormat.BLACK);
            return this;
        }

        @Override
        public Builder<String, CommandSender> blue() {
            elements.add(TextFormat.BLACK);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_aqua() {
            elements.add(TextFormat.DARK_AQUA);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_blue() {
            elements.add(TextFormat.DARK_BLUE);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_gray() {
            elements.add(TextFormat.DARK_GRAY);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_green() {
            elements.add(TextFormat.DARK_GREEN);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_purple() {
            elements.add(TextFormat.DARK_PURPLE);
            return this;
        }

        @Override
        public Builder<String, CommandSender> dark_red() {
            elements.add(TextFormat.DARK_RED);
            return this;
        }

        @Override
        public Builder<String, CommandSender> gold() {
            elements.add(TextFormat.GOLD);
            return this;
        }

        @Override
        public Builder<String, CommandSender> gray() {
            elements.add(TextFormat.GOLD);
            return this;
        }

        @Override
        public Builder<String, CommandSender> green() {
            elements.add(TextFormat.GREEN);
            return this;
        }

        @Override
        public Builder<String, CommandSender> light_purple() {
            elements.add(TextFormat.LIGHT_PURPLE);
            return this;
        }

        @Override
        public Builder<String, CommandSender> red() {
            elements.add(TextFormat.RED);
            return this;
        }

        @Override
        public Builder<String, CommandSender> reset() {
            elements.add(TextFormat.RESET);
            return this;
        }

        @Override
        public Builder<String, CommandSender> white() {
            elements.add(TextFormat.WHITE);
            return this;
        }

        @Override
        public Builder<String, CommandSender> yellow() {
            elements.add(TextFormat.YELLOW);
            return this;
        }

        @Override
        public Builder<String, CommandSender> append(Object... content) {
            return this;
        }

        @Override
        public Builder<String, CommandSender> appendJoining(Object delimiter, Object... content) {
            return this;
        }

        @Override
        public Builder<String, CommandSender> onHoverShowText(String content) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onHoverShowText(Builder<String, CommandSender> builder) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onClickSuggestCommand(String command) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onClickRunCommand(String command) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onClickExecuteCallback(Consumer<CommandSender> callback) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onClickOpenUrl(URL url) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder<String, CommandSender> onClickOpenUrl(String url) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String build() {

            if (elements.isEmpty()) {
                return "";
            }

            StringBuilder builder = new StringBuilder();

            for (Object o : elements) {
                if (o instanceof Builder) {
                    builder.append(o);
                }
            }
            return builder.toString();
        }

        @Override
        public void sendTo(CommandSender commandSender) {
            commandSender.sendMessage(build());
        }
    }
}
