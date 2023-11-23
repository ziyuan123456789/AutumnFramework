import org.junit.Test;
import org.junit.runner.Computer;

/**
 * @author wangzhiyi
 * @since 2023.11
 */

public class test {
    class Computer {
        // 必需的参数
        private final String cpu;
        private final String ram;
        // 可选的参数
        private final int usbCount;
        private final String keyboard;
        private final String display;

        private Computer(Builder builder) {
            this.cpu = builder.cpu;
            this.ram = builder.ram;
            this.usbCount = builder.usbCount;
            this.keyboard = builder.keyboard;
            this.display = builder.display;
        }

        // ... 此处省略 getter 方法 ...

        public final class Builder {
            // 必需的参数
            private final String cpu;
            private final String ram;
            // 可选的参数，可以给予默认值
            private int usbCount = 0; // 默认值例如 0
            private String keyboard = ""; // 默认值例如空字符串
            private String display = ""; // 默认值例如空字符串

            public Builder(String cpu, String ram) {
                this.cpu = cpu;
                this.ram = ram;
            }

            public Builder setUsbCount(int usbCount) {
                this.usbCount = usbCount;
                return this;
            }

            public Builder setKeyboard(String keyboard) {
                this.keyboard = keyboard;
                return this;
            }

            public Builder setDisplay(String display) {
                this.display = display;
                return this;
            }

            public Computer build() {
                // 在构建之前可以添加校验逻辑
                return new Computer(this);
            }
        }

        @Override
        public String toString() {
            return "Computer{" +
                    "cpu='" + cpu + '\'' +
                    ", ram='" + ram + '\'' +
                    ", usbCount=" + usbCount +
                    ", keyboard='" + keyboard + '\'' +
                    ", display='" + display + '\'' +
                    '}';
        }
    @Test
    public void test1() {
        Computer c=new Computer.Builder("intel","8g").setDisplay("三星").build();
        System.out.println(c.toString());
    }

}}
