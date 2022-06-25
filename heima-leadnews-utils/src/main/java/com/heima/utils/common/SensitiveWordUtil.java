package com.heima.utils.common;


import java.util.*;

public class SensitiveWordUtil {

    public static Map<String, Object> dictionaryMap = new HashMap<>();


    /**
     * 生成关键词字典库
     * //冰毒、大麻、大坏蛋
     *
     * @param words
     * @return
     */
    public static void initMap(Collection<String> words) {
        if (words == null) {
            System.out.println("敏感词列表不能为空");
            return;
        }

        // map初始长度words.size()，整个字典库的入口字数(小于words.size()，因为不同的词可能会有相同的首字)
        Map<String, Object> map = new HashMap<>(words.size());
        // 遍历过程中当前层次的数据
        Map<String, Object> curMap = null;
        Iterator<String> iterator = words.iterator();

        while (iterator.hasNext()) {
            //冰毒
            String word = iterator.next();
            curMap = map;
            int len = word.length();
            for (int i = 0; i < len; i++) {
                // 遍历每个词的字
                String key = String.valueOf(word.charAt(i));
                // 当前字在当前层是否存在, 不存在则新建, 当前层数据指向下一个节点, 继续判断是否存在数据
                Map<String, Object> wordMap = (Map<String, Object>) curMap.get(key);
                if (wordMap == null) {
                    // 每个节点存在两个数据: 下一个节点和isEnd(是否结束标志)
                    wordMap = new HashMap<>(2);
                    wordMap.put("isEnd", "0");
                    curMap.put(key, wordMap);
                }
                curMap = wordMap;
                // 如果当前字是词的最后一个字，则将isEnd标志置1
                if (i == len - 1) {
                    curMap.put("isEnd", "1");
                }
            }
        }

        dictionaryMap = map;
    }

    /**
     * 搜索文本中某个文字是否匹配关键词
     *
     * @param text
     * @param beginIndex
     * @return
     */
    private static int checkWord(String text, int beginIndex) {
        //敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        //匹配到的敏感字的个数，也就是敏感词长度
        int length = 0;
        //从根Map开始查找
        Map nowMap = dictionaryMap;
        for (int i = beginIndex; i < text.length(); i++) {
            //被判断语句的第i个字符开始
            String key = String.valueOf(text.charAt(i));
            //获取指定key，并且将敏感库指针指向下级map
            nowMap = (Map) nowMap.get(key);
            if (nowMap != null) {//存在，则判断是否为最后一个
                //找到相应key，匹配长度+1
                length++;
                //如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    //结束标志位为true
                    flag = true;
                }
            } else {
                //敏感库不存在，直接中断
                break;
            }
        }
        if (length < 2 || !flag) {
            //长度必须大于等于1才算是词，字的话就不必这么折腾了
            length = 0;
        }
        return length;
    }

    /**
     * 获取匹配的关键词和命中次数
     *
     * @param text
     * @return
     */
    public static Map<String, Integer> matchWords(String text) {
        Map<String, Integer> wordMap = new HashMap<>();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            int wordLength = checkWord(text, i);
            if (wordLength > 0) {
                String word = text.substring(i, i + wordLength);
                // 添加关键词匹配次数
                if (wordMap.containsKey(word)) {
                    wordMap.put(word, wordMap.get(word) + 1);
                } else {
                    wordMap.put(word, 1);
                }

                i += wordLength - 1;
            }
        }
        return wordMap;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("冰毒");
        initMap(list);
        String content = "我是一个好人，买卖冰毒是违法的冰毒";
        Map<String, Integer> map = matchWords(content);
        System.out.println(map);
    }
}
