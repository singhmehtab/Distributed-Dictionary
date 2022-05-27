import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class Dictionary {

    private HashMap<String, ArrayList<Integer>> dictionary;

    public Dictionary(){
        this.dictionary = new HashMap<>();
    }

    public void add(String key, Integer value){
        if(dictionary.containsKey(key)){
            dictionary.get(key).add(value);
        }
        else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(value);
            dictionary.put(key, list);
        }
    }

    public void set(String key, Integer value){
        ArrayList<Integer> list = new ArrayList<>();
        list.add(value);
        dictionary.put(key, list);
    }

    public void delete(String key){
        dictionary.remove(key);
    }

    public String listKeys(){
        StringBuilder sb = new StringBuilder();
        ArrayList<String> list = new ArrayList<String>(dictionary.keySet());
        sb.append(list.get(0));
        for(int i=1;i<list.size();i++){
            sb.append(",").append(list.get(i));
        }
        return sb.toString();
    }

    public String getValue(String key){
        if(dictionary.containsKey(key)){
            return dictionary.get(key).get(0).toString();
        }
        else return "error";
    }

    public String getValues(String key){
        if(dictionary.containsKey(key)){
            StringBuilder sb = new StringBuilder();
            ArrayList<Integer> list = dictionary.get(key);
            sb.append(list.get(0));
            for(int i=1;i<list.size();i++){
                sb.append(",").append(list.get(i));
            }
            return sb.toString();
        }
        else return "error";
    }

    public String sum(String key){
        if(dictionary.containsKey(key)) {
            return String.valueOf(dictionary.get(key).stream().mapToInt(i -> i).sum());
        }
        else return "error";
    }

    public String max(String key){
        if(dictionary.containsKey(key)) {
            return String.valueOf(Collections.max(dictionary.get(key)));
        }
        else return "error";
    }

    public void resetAll(){
        dictionary.clear();
    }

}
