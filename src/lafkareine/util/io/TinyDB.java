package lafkareine.util.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;

/** シンプルかつ低機能なファイルへの入出力を提供します
 *
 *
 * 複数の書き込みが同時に行われる場合、個別にsetを呼び出すのではなくtransactionを利用するほうが効率的です
 * また、値が変更された直後にファイルへの書き込みが行われます
 * 外部で値が変更された場合、reloadによって外部の変更を反映することができます
 * */
public final class TinyDB {
    private final Path path;

    private final Map<String, String> DB = new HashMap<>();

    public TinyDB(Class base, String name) {

        path = ResourcePath.getJarPath(base).resolve(name+".tinydb");

        reload();
    }

    public TinyDB(Path base, String name) {

        path = base.resolve(name+".tinydb");

        reload();
    }

    public final class Writer{
        private Writer(){}

        public void set(String name, String value){
            if(name == null||value == null){
                throw new IllegalArgumentException("name and value can't null");
            }
            DB.put(name,value);
            isWritten = true;
        }

        public void set(String script){
            String[] split = script.split("=");
            set(split[0],split[1]);
        }

        public void set(String name, boolean value){
            set(name,Boolean.toString(value));
        }

        public void set(String name, byte value){
            set(name,Byte.toString(value));
        }

        public void set(String name, short value) {
            set(name,Short.toString(value));
        }

        public void set(String name, int value){
            set(name,Integer.toString(value));
        }

        public void set(String name, long value){
            set(name,Long.toString(value));
        }

        public void set(String name, float value){
            set(name,Float.toString(value));
        }

        public void set(String name, double value){
            set(name,Double.toString(value));
        }

        private boolean isWritten;
    }

    private final Writer writer = new Writer();

    public void transaction(Consumer<Writer> action){
        writer.isWritten = false;
        action.accept(writer);
        if(writer.isWritten){
            StringBuilder builder = new StringBuilder();
            for(Map.Entry<String, String> e :DB.entrySet()){
                builder.append(e.getKey()).append('=').append(e.getValue()).append('\n');
            }
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, builder.toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reload(){
        DB.clear();
        if(Files.exists(path)) {
            try {
                String script = Files.readString(path);
                script.lines().forEach(x -> {
                    String[] info = x.split("=");
                    DB.put(info[0], info[1]);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<String> get(String name){
        return Optional.ofNullable(DB.get(name));
    }

    public Optional<Boolean> getBool(String name){
        String value = DB.get(name);
        return (value != null)?Optional.of(Boolean.parseBoolean(value)):Optional.empty();
    }

    public Optional<Byte> getByte(String name){
        String value = DB.get(name);
        return (value != null)?Optional.of(Byte.parseByte(value)):Optional.empty();
    }

    public Optional<Short> getShort(String name){
        String value = DB.get(name);
        return (value != null)?Optional.of(Short.parseShort(value)):Optional.empty();

    }

    public OptionalInt getInt(String name){
        String value = DB.get(name);
        return (value != null)?OptionalInt.of(Integer.parseInt(value)):OptionalInt.empty();
    }

    public OptionalLong getLong(String name){
        String value = DB.get(name);
        return (value != null)?OptionalLong.of(Long.parseLong(value)):OptionalLong.empty();
    }

    public Optional<Float> getFloat(String name){
        String value = DB.get(name);
        return (value != null)?Optional.of(Float.parseFloat(value)):Optional.empty();
    }

    public OptionalDouble getDouble(String name){
        String value = DB.get(name);
        return (value != null)?OptionalDouble.of(Long.parseLong(value)):OptionalDouble.empty();
    }

    public void set(String name, String value){
        transaction(x->x.set(name, value));
    }

    public void set(String script){
        transaction(x->x.set(script));
    }

    public void set(String name, boolean value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, byte value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, short value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, int value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, long value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, float value){
        transaction(x->x.set(name, value));
    }

    public void set(String name, double value){
        transaction(x->x.set(name, value));
    }
}
