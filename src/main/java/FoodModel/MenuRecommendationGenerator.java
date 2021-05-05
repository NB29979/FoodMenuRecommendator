package FoodModel;

import java.util.*;

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MenuRecommendationGenerator {
    private FoodScoreCalculator foodColorScoreCalculator;
    // 隣接行列
    private double[][] costs;
    private List<Food> foodList;
    private int nodeSize;
    private int offset;

    public MenuRecommendationGenerator() {
        foodList = new ArrayList<>(
                Arrays.asList(FoodInfo.foodList.stream()
                        .map(Food::clone)
                        .toArray(Food[]::new)));

        // 副菜は2つなのでオフセットとる
        offset = (int)foodList.stream().filter(food -> food.layout==1).count();
        // startとgoalの2ノードが追加されている
        nodeSize =foodList.size()+offset+2;
    }

    private void initAdjacencyMatrix(List<String> _selectedFoodNames){
        class SelectedFoodParam{
            private String name;
            private int index;
            private int layout;
            private int layoutType;
            private SelectedFoodParam(String _name, int _index, int _layout, int _layoutType){
                this.name = _name;
                this.index = _index;
                this.layout = _layout;
                this.layoutType = _layoutType;
            }
        }
        List<SelectedFoodParam> selectedFoodParams_ =
                _selectedFoodNames.stream()
                    .map(foodName -> {
                        int index_ = IntStream.range(0, foodList.size())
                                .filter(i -> foodName.equals(foodList.get(i).name))
                                .findFirst().orElse(-1);
                        int selectedLayout_ = foodList.get(index_).layout;
                        int selectedLayoutType_ = (int)Math.log10(selectedLayout_);
                        return new SelectedFoodParam(foodName, index_+1, selectedLayout_, selectedLayoutType_);
                    })
                    .collect(Collectors.toList());

        costs = new double[nodeSize][nodeSize];

        HashMap<Integer, List<Integer>>categorizedFoodIndexes = new HashMap<>();

        List<Integer> selectedFoodLayoutTypes_ = selectedFoodParams_.stream()
                .map(foodParam->foodParam.layoutType)
                .filter(layoutType->layoutType!=0)
                .collect(Collectors.toList());
        List<Integer> selectedFoodLayouts_ = selectedFoodParams_.stream()
                .map(foodParam->foodParam.layout)
                .collect(Collectors.toList());
        long selectedSubFoodLayoutCount = selectedFoodLayouts_.stream()
                .filter(layout->layout==1).count();
        List<String> selectedFoodNames = selectedFoodParams_.stream()
                .map(foodPram->foodPram.name)
                .collect(Collectors.toList());

        for (int i = 0; i < foodList.size(); ++i) {
            Food food_ = foodList.get(i);
            int foodLayout_ = food_.layout;
            String foodName_ = food_.name;
            // 選択されたメニューと同じようなレイアウトは選ばない
            // 選択されたメニューのみを選択する
            if ((foodLayout_!=1 &&
                    (!selectedFoodLayoutTypes_.contains((int) Math.log10(foodLayout_)) ||
                    selectedFoodNames.contains(foodName_))) ||
                // 副菜だけは副菜-副菜があるので全て通す．ただし選ばれた副菜は含まない(副菜1つのみが選ばれた場合)
                // 副菜が2つ選ばれたときは例外
                (foodLayout_ ==1 &&
                    (selectedSubFoodLayoutCount == 0 ||
                    selectedSubFoodLayoutCount == 1 && !selectedFoodNames.contains(foodName_)) ||
                    selectedSubFoodLayoutCount == 2 && selectedFoodNames.contains(foodName_))){
                        // どんぶり->副菜ルート
                if((selectedFoodLayouts_.contains(1110) && (selectedFoodNames.contains(foodName_) || foodLayout_==1)) ||
                        // 主食-汁物-主菜->どんぶり以外ルート
                        (selectedFoodLayouts_.stream().max(Comparator.naturalOrder()).get()<=1000 && foodLayout_ != 1110) ||
                        // 副菜のみが選ばれたパターン
                        (selectedFoodLayouts_.stream().max(Comparator.naturalOrder()).get()<=1)) {
                    if (categorizedFoodIndexes.containsKey(foodLayout_))
                        categorizedFoodIndexes.get(foodLayout_).add(i+1);
                    else
                        categorizedFoodIndexes.put(foodLayout_, new ArrayList<>(Arrays.asList(i+1)));
                }
            }
        }
        // エッジコストの登録
        categorizedFoodIndexes.entrySet().stream()
            .filter(entry->!entry.getValue().isEmpty())
            .forEach(entry->{
                int key_=entry.getKey();
                List<Integer> indexes_=entry.getValue();
                // start - 主食，丼
                if(key_>=1000){
                    // スタートとつなぐ
                    indexes_.forEach(idx->
                            registerEdgeCosts(idx, Arrays.asList(0), 0));
                }
                // 主食 - 汁物
                else if(key_>=100 && categorizedFoodIndexes.containsKey(1000)){
                    indexes_.forEach(idx->
                            registerEdgeCosts(idx, categorizedFoodIndexes.get(1000), 0));
                }
                // 汁物 - 主菜
                else if(key_>=10 && categorizedFoodIndexes.containsKey(100)){
                    indexes_.forEach(idx->
                            registerEdgeCosts(idx, categorizedFoodIndexes.get(100), 0));
                }
                else{
                    // 丼 - 副菜
                    if(categorizedFoodIndexes.containsKey(1110))
                        indexes_.forEach(idx->
                                registerEdgeCosts(idx, categorizedFoodIndexes.get(1110), 0));
                    // 主菜 - 副菜
                    if(categorizedFoodIndexes.containsKey(10))
                        indexes_.forEach(idx->
                                registerEdgeCosts(idx, categorizedFoodIndexes.get(10), 0));

                    // 副菜が2つ選択されている場合には未対応
                    // 副菜 - 副菜
                    if(categorizedFoodIndexes.containsKey(1)) {
                        // 副菜がユーザにより選択されていたとき
                        if(selectedSubFoodLayoutCount==1 && selectedFoodLayouts_.contains(1)) {
                            selectedFoodParams_.stream()
                                    .filter(foodParam -> foodParam.layout == 1)
                                    .forEach(foodParam -> {
                                        registerEdgeCosts(foodParam.index + offset, indexes_, offset);
                                        registerEdgeCosts(nodeSize - 1, Arrays.asList(foodParam.index + offset), 0);
                                    });
                        }
                        else {
                            indexes_.forEach(idx -> registerEdgeCosts(idx + offset, categorizedFoodIndexes.get(1), offset));
                            registerEdgeCosts(nodeSize - 1, indexes_.stream().map(idx -> idx + offset).collect(Collectors.toList()), 0);
                        }
                    }
                }
            });
    }
    public void removeFoodNameFromMatrix(String _selectedFoodName){
        int selectedIndex_ = IntStream.range(0, foodList.size())
                .filter(i->_selectedFoodName.equals(foodList.get(i).name))
                .findFirst().orElse(-1);
        int selectedLayout_ = foodList.get(selectedIndex_).layout;
        int selectedLayoutType_ = (int)Math.log10(selectedLayout_);

        IntStream.range(0, nodeSize-1)
                .forEach(i->costs[i][selectedIndex_+1] = 0.0);
        if(selectedLayoutType_==0)
            IntStream.range(0, nodeSize-1)
                .forEach(i->costs[i][selectedIndex_+offset+1] = 0.0);
    }
    private void registerEdgeCosts(int _idx, List<Integer> _foodIndexes, int _offset){
        _foodIndexes.stream()
                // 副菜-副菜で同じものをつながないように
                .filter(foodIdx -> _idx - _offset != foodIdx)
                .forEach(foodIdx -> costs[foodIdx][_idx] = 1.0);
    }
    public void init(String _gender, List<String> _selectedFoodNames, double _cost){
        foodColorScoreCalculator =
                new FoodScoreCalculator(new BestScoreModel(_gender), _cost);
        initAdjacencyMatrix(_selectedFoodNames);
    }
    public List<SelectedFoods> provideMenu(){
        class SearchNode{
            private int index;
            private List<SelectedFoods> selectedMenuList;
            private SearchNode(int _index, List<SelectedFoods> _selectedMenuList){
                this.index=_index;
                this.selectedMenuList = _selectedMenuList;
            }
        }
        List<SelectedFoods>[] nodes_ = new ArrayList[nodeSize];
        Arrays.fill(nodes_, new ArrayList<>(Arrays.asList(new SelectedFoods())));
        int offset_=(int)foodList.stream().filter(food -> food.layout==1).count();

        Queue<SearchNode>que=new ArrayDeque<>();
        que.add(new SearchNode(0, new ArrayList<>(Arrays.asList(new SelectedFoods()))));
        while(!que.isEmpty()){
            SearchNode searchNode_=que.poll();

            List<SelectedFoods> searchNodeMenuList_ = searchNode_.selectedMenuList;
            int nodeIndex_ = searchNode_.index;

            for(int x=1;x<nodeSize;++x){
                if(costs[nodeIndex_][x]==1.0){
                    List<SelectedFoods> updatedFoodMenuList_;
                    if(x != nodeSize-1) {
                        final Food selectedFood_ = x > foodList.size() && x != nodeSize-1 ?
                                foodList.get(x-offset_-1) : foodList.get(x-1);

                        updatedFoodMenuList_ = searchNodeMenuList_.stream()
                                .map(foodMenu -> foodMenu.clone().updateAttributes(selectedFood_))
                                .collect(Collectors.toList());

                        nodes_[x] = updatedFoodMenuList_;
                    }
                    // ゴールノードに到達したとき
                    else if(x==nodeSize-1){
                        updatedFoodMenuList_ = searchNodeMenuList_;
                        nodes_[x].addAll(updatedFoodMenuList_);
                    }
                    else continue;

                    que.add(new SearchNode(x, updatedFoodMenuList_));
                }
            }
        }
        return nodes_[nodeSize-1].stream()
                .sorted(Comparator.comparingDouble(foodColorScoreCalculator::score))
                .distinct()
                .collect(Collectors.toList());
    }
}
