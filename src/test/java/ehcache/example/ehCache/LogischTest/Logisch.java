package ehcache.example.ehCache.LogischTest;

import ehcache.example.ehCache.Dto.CreateBookDto;
import ehcache.example.ehCache.Entity.Role;
import ehcache.example.ehCache.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Logisch {

    List<List<Integer>> Lists=new ArrayList<>();

    @BeforeEach
    void setUp() {


        List<Integer> L1=List.of(1,1,2,3,5) ;


        List<Integer> L2=List.of(1,5);


Lists.add(L1);
Lists.add(L2);


    }

    @Test
    void MaxListOfIntegers (  ) {

        int MaxList = 0;

        MaxList = Lists.stream().mapToInt(list->list.stream().mapToInt(Integer::intValue)
                .sum())
                .sum()
                ;

        System.out.println(MaxList);

    }

    @Test
    void MaxListOfString (  ) {

        int MaxNameLength = 0 ;
        List<String> namen = List.of("Anna", "Ben", "Christoph");
         MaxNameLength = namen.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);



          String a = namen.get(0);

          a.indexOf('a');
          a+=1;
        System.out.println("a+1="+a);

        System.out.println("last endew ===" ) ;
        char lastOP[] ={' '};
if(a.charAt(0)=='A') System.out.println("a="+a.charAt(0));

        System.out.println(MaxNameLength);

String []strs ={"Anna","Ben"};
        System.out.println(strs.length);
        strs[0].length(); 

    }
int []s=new int[10];

}
