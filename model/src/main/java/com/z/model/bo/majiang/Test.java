package com.z.model.bo.majiang;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.z.model.proto.CommonGame;

import java.util.*;

public class Test {

    public static Table<Integer,Integer,Integer> table = HashBasedTable.create();
    // 牌的二维数组
    private List<List<MJBo>> board;
    static List<CommonGame.MJ> list = new ArrayList<>();
    static{
        int index = 0;
        for (CommonGame.MJ value : CommonGame.MJ.values()) {
            if(value == CommonGame.MJ.DEFAULT || value == CommonGame.MJ.UNRECOGNIZED) continue;
            if(index++<6) continue;
            list.add(value);
        }
    }

    /**
     *  初始化数组
     */
    public void init() {
        // 初始化五排
        board = new ArrayList<>();
        List<MJBo> list1 = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            CommonGame.MJ mj = random();
            MJBo bo = new MJBo(mj,0,i);
            list1.add(bo);
        }
        MJBo bo1 = new MJBo(CommonGame.MJ.B_3,0,1);
        list1.add(bo1);
        bo1 = new MJBo(CommonGame.MJ.B_3,0,2);
        list1.add(bo1);
        bo1 = new MJBo(CommonGame.MJ.B_3,0,3);
        list1.add(bo1);
        board.add(list1);

        for (int i = 1; i < 4; i++) {
            List<MJBo> list2 = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                CommonGame.MJ mj = random();
                MJBo bo = new MJBo(mj,i,j);
                list2.add(bo);
            }
            board.add(list2); // 第二到第四排 5 个
        }
        list1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CommonGame.MJ mj = random();
            MJBo bo = new MJBo(mj,4,i);
            list1.add(bo);
        }
        board.add(list1); // 第五排 4 个
    }

    /**
     * 随机符号
     * @return
     */
    public CommonGame.MJ random(){
        Collections.shuffle(list);
        return list.get(0);
    }
    public CommonGame.MJ random(Set<CommonGame.MJ> excludes){
        List<CommonGame.MJ> list1 = new ArrayList<>(list);
        list1.removeAll(excludes);
        Collections.shuffle(list1);
        return list1.get(0);
    }


    public Map<CommonGame.MJ, MjGoalTest> check() {
        Map<CommonGame.MJ, MjGoalTest> map = new HashMap<>();
        List<MJBo> list1  = board.get(0);
        Map<CommonGame.MJ,List<MJBo>> map1 = new HashMap<>();
        for (MJBo mj : list1) {
           List<MJBo>  tmpList =  map1.getOrDefault(mj.getType(),new ArrayList<>());
           map1.putIfAbsent(mj.getType(),tmpList);
           tmpList.add(mj);
        }
        for (CommonGame.MJ mj : map1.keySet()) {
            List<MJBo> tmpList1 =  map1.get(mj);
            int size1 = tmpList1.size();
            MjGoalTest bo = map.getOrDefault(mj,new MjGoalTest(mj,1,1));
            map.putIfAbsent(mj,bo);
            int rate = bo.getRate();
            for (int i = 1; i < 5 ; i++) {
                List<MJBo> tmpList  = board.get(i);
                int c = 0;
                List<MJBo> tmpList2 =  map1.get(mj);
                for (MJBo e : tmpList) {
                    if(e.getType()== mj) {
                        c++;
                        tmpList2.add(e);
                    }
                }
                if(c<1){
                    break;
                }
                rate = rate* c;
                bo.addC();
                bo.addPoint(tmpList2);
            }
            rate = rate*size1;
            bo.setRate(rate);
            bo.addPoint(tmpList1);
        }
        Iterator<Map.Entry<CommonGame.MJ, MjGoalTest>> iter =  map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<CommonGame.MJ, MjGoalTest> entry = iter.next();
            MjGoalTest bo = entry.getValue();
            if(bo.getC()<3) {
                iter.remove();
            }
        }

        map.forEach((mj,bo)->{
            System.err.println(mj+"===>"+bo);
        });
        return map;
    }

    // 消除符号并让下方的符号前移
    private boolean move(Map<CommonGame.MJ, MjGoalTest> map, boolean b_exlude) {
        boolean b = false;
        for (List<MJBo> mjBos : board) {
             Iterator<MJBo> iter =  mjBos.iterator();
             while (iter.hasNext()) {
                 MJBo bo = iter.next();
                 CommonGame.MJ mj = bo.getType();
                 MjGoalTest mjGoalTest = map.get(mj);
                 if(mjGoalTest == null) continue;
                 if(mjGoalTest.getPoints().isEmpty()) continue;
                 for (MJBo p : mjGoalTest.getPoints()) {
                     if(bo.getX() == p.getX() && bo.getY() == p.getY()) {
                         iter.remove();
                         break;
                     }
                 }
             }
        }
        Set<CommonGame.MJ> excludes1 = new HashSet<>();
        for (int i = 0; i < 5 ; i++) {
            List<MJBo> mjBos = board.get(i);
            int maxSize  = 5;
            if(i == 0 || i == 4){
                maxSize = 4;
            }
            int size = maxSize-mjBos.size();
            for (int j = 0; j < size; j++) {
                CommonGame.MJ mj = random();
                if((i== 1 || i ==2) && b_exlude){
                    mj = random(excludes1);
                }else{
                    mj = random();
                }
                MJBo bo = new MJBo(mj,i,j);
                mjBos.add(bo);
                b =true;
            }
            if(i == 0 ){
                for (MJBo mjBo : board.get(0)) {
                    excludes1.add(mjBo.getType());
                }
            }

        }
        StringJoiner sj = new StringJoiner(",");
        for (CommonGame.MJ exclude : excludes1) {
            sj.add(exclude.getNumber()+"");
        }
        System.err.println("excludes: "+sj.toString());
        return b;
    }

//    // 打印牌面
    public void printBoard() {
        for (List<MJBo> row : board) {
            for (MJBo tile : row) {
                System.err.print(tile+" ");
            }
            System.err.println();
        }
    }


    public static void main(String[] args) {
        Test game = new Test();

        for (int i = 0; i < 20; i++) {
            game.init();
//            System.err.println("-----------------");
//         System.err.println("初始牌面：");
//        game.printBoard();
//        System.err.println("检查移除：");
            Map<CommonGame.MJ, MjGoalTest> map = game.check();
            int index = 0;
            while (!map.isEmpty()){
                boolean b = game.move(map,true);
//             System.err.println("消除后的牌面：");
//             game.printBoard();
                if(!b){
                    break;
                }
                map = game.check();
//                System.err.println("=================================");
                index++;

            }
            System.err.println("消除的次数："+index);
        }
    }

    // 检查并消除连续符号的三排并且标识
//    public void check1() {
//        for (int col = 0; col < 5; col++) {  // 遍历每列
//            // 记录每一列的连续符号
//            CommonGame.MJ prev = CommonGame.MJ.MJ_DEFAULT;
//            int count = 1;
//            int rowSize =5;
//            if(col == 0 || col == 4) {
//                rowSize = 4;
//            }
//            for (int row = 0; row < rowSize; row++) {
////                System.err.println("i:" + col + " j:" + row);
//                CommonGame.MJ current = board.get(col).get(row);
//                if (current == prev && current != CommonGame.MJ.MJ_DEFAULT) {
//                    count++;
//                } else {
//                    if (count >= 3) {
//                        move(prev, col, row - count, row - 1);  // 如果连续 >= 3 则消除
//                    }
//                    prev = current;
//                    count = 1;
//                }
//            }
//            if (count >= 3) {  // 如果最后一组符号满足条件
//                move(prev, col, 5 - count, 5 - 1);
//            }
//        }
//    }
}
