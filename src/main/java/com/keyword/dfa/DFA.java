package com.keyword.dfa;

import com.keyword.util.EmptyUtil;

import java.util.*;

/**
 * DFA算法
 *
 * @author 谢俊权
 * @create 2016/2/23 16:58
 */
public class DFA {

    private DFAState rootDFAState = new DFAState(null, false, null);

    private Set<String> keywordSet = new HashSet<String>();

    /**
     * 搜索在给定的文字中有哪些敏感的关键字
     * @param word
     * @return
     */
    public Set<String> search(String word){
        Set<String> result = new HashSet<String>();
        searchKeyword(result, word);
        return result;
    }

    private void searchKeyword(Set<String> result, String word){

        DFAState currState = rootDFAState;
        int backNum = 0;
        for(int i = 0; i < word.length() && !currState.isEnd; i++){
            Character name = word.charAt(i);
            if(currState.nextStateMap.containsKey(name)){
                currState = currState.nextStateMap.get(name);
                backNum++;
            }else{
                if(backNum > 0){
                    i = i - backNum;
                    backNum = 0;
                }
                currState = rootDFAState;
            }
        }
        if(currState.isEnd){
            String keyword = getkeyword(currState);
            result.add(keyword);
            word = word.replaceAll(keyword, "");
            searchKeyword(result, word);
        }
    }

    private String getkeyword(DFAState state){
        DFAState currState = state;
        StringBuilder keywordBuilder = new StringBuilder();
        while(!EmptyUtil.isEmpty(currState.parentState)){
            Character name = currState.stateName;
            keywordBuilder.append(name);
            currState = currState.parentState;
        }
        return keywordBuilder.reverse().toString();
    }

    /**
     * 添加敏感关键字集合
     * @param keywordSet
     */
    public void setKeywords(Set<String> keywordSet) {
        if(!EmptyUtil.isEmpty(keywordSet)){
            Iterator<String> it = keywordSet.iterator();
            while(it.hasNext()){
                String name = it.next();
                setKeyword(name);
            }
        }
    }

    /**
     * 添加敏感关键字
     * @param keyword
     */
    public void setKeyword(String keyword){
        keywordSet.add(keyword);
    }

    /**
     * 构建关键字状态图
     */
    public void build(){
        clear();
        Iterator<String> it = keywordSet.iterator();
        while(it.hasNext()){
            String keyword = it.next();
            setkeywordToDFAState(keyword);
        }
    }

    private void setkeywordToDFAState(String keyword){
        DFAState currState = rootDFAState;
        int length = keyword.length();
        for(int i = 0; i < length; i++){
            Character name = keyword.charAt(i);
            if(!currState.nextStateMap.containsKey(name)){
                boolean isEnd = (i == length - 1) ? true : false;
                DFAState newState = new DFAState(name, isEnd, currState);
                currState.nextStateMap.put(name, newState);
            }
            currState = currState.nextStateMap.get(name);
        }
    }

    /**
     * 清除当前的关键字状态图
     */
    public void clear(){
        clear(rootDFAState);
    }

    private void clear(DFAState state){
        for (Character name : state.nextStateMap.keySet()) {
            DFAState currState = state.nextStateMap.get(name);
            clear(currState);
        }
        state = null;
    }



    private class DFAState{
        public Character stateName;
        public boolean isEnd;
        public Map<Character, DFAState> nextStateMap = new HashMap<Character, DFAState>();
        public DFAState parentState;


        public DFAState(Character stateName, boolean isEnd, DFAState parentState) {
            this.stateName = stateName;
            this.isEnd = isEnd;
            this.parentState = parentState;
        }
    }

    public static void main(String[] args){

        DFA dfa = new DFA();
        Set<String> keywordSet = new HashSet<String>();
        keywordSet.add("毛泽东");
        keywordSet.add("江泽民");
        keywordSet.add("中国共产党");
        keywordSet.add("法轮功哈哈哈啊哈");
        dfa.setKeywords(keywordSet);
        dfa.build();
        System.out.println(dfa.search("共产党中国共产党啊毛泽东就算开绿灯飞机江泽民阿喀琉共党jiangze斯节日哦maozdong权威 "));
    }


}
