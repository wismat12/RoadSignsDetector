package signArea;

import config.Config;
import representation.Image;

public class PatternChecker {



    static public int recognisePattern(Image img){

        for(int i = 0; i < Config.PATTERN_BASE.length ; i++){
            if(img.isEqual(Config.PATTERN_BASE[i], Config.INEQUALITY_PERCENTAGE))
                return i;
        }
        return -1;
    }
}
