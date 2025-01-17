package com.we.estimate.Controller;

import com.we.estimate.Entity.ShaIn;
import com.we.estimate.Entity.ShaInDataBase;
import com.we.estimate.Search.ShaInSearchResult;
import com.we.estimate.Service.ShaInService;
import com.we.estimate.Tools.UserTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/shaIn")
@Slf4j
public class ShaInController {

    private ShaIn shaInSearchCondition = new ShaIn();
    private final UserTools userTools = new UserTools();

    @Autowired
    private ShaInService shaInService;

    public ShaInController() {
    }

    // 社員入力画面
    @GetMapping("/add")
    public String showShaInForm(Model model) {

        // shaIn　入力データ保存モデル
        model.addAttribute("shaIn", new ShaIn());
        
        // 新規モード
        model.addAttribute("mode", "add");

        return "shainadd";
    }

    // 社員一覧画面 ポップアップモード
    @GetMapping("/list-popup")
    public String showShaInListPopup(Model model) {

        // shaInSearch 検索条件のモデル
        // shaInModel 検索データのモデル
        model.addAttribute("shaInSearch", new ShaIn());
        model.addAttribute("shaInModel", new ShaInDataBase());

        // ポップアップモード　表示
        model.addAttribute("mode", "popup");

        return "shainlist";
    }

    // 社員入力画面 シートモデル
    @PostMapping("/sheet")
    public String showShaInFormSheet(Model model) {

        // shaIn　入力データ保存モデル
        model.addAttribute("shaIn", new ShaIn());

        // シートモード　表示
        model.addAttribute("mode", "sheet");

        return "shainadd";
    }

    // 新規社員入力処理
    @PostMapping("/submit")
    public String submitShaInForm(@ModelAttribute("shaIn") ShaIn newshaIn) {

        // Mybatis引数タイプ データ保存
        List<ShaIn> shaInsList = new ArrayList<>();

        // 時間設定
        LocalDateTime now = LocalDateTime.now();
        newshaIn.setTdInsert(Timestamp.valueOf(now));

        // Listにデータ追加
        shaInsList.add(newshaIn);

        //　データ保存
        shaInService.saveShaIn(shaInsList);

        return "success2";
    }

    // 社員一覧画面
    @GetMapping("/list")
    public String showShaInList(Model model, HttpServletResponse response) {

        // キャッシュ禁用
        userTools.disableCache(response);

        // shaInSearch 検索条件のモデル
        // shaInModel 検索データのモデル
        model.addAttribute("shaInSearch", new ShaIn());
        model.addAttribute("shaInModel", new ShaInDataBase());

        // 参照モード 初期表示
        model.addAttribute("mode", "reference");

        return "shainlist";
    }

    @PostMapping("list/search")
    @ResponseBody
    public ShaInSearchResult shaInSearchResult(@ModelAttribute("shaInSearch") ShaIn shaIn) {

        // Mybatis引数タイプ 検索
        Map<String, Object> paramMap = new HashMap<>();

        ShaInSearchResult shaInSearchResult = new ShaInSearchResult();
        // 検索条件保存　@GetMapping("list/search/page")ページング検索用
        shaInSearchCondition = shaIn;

        // ページ1
        Integer searchOffset = 1;
        Integer searchLimit = 10;

        // ページング検索　引数
        // offset オフセット
        // limit 検索最大数
        //　shaIn　検索条件
        paramMap.put("offset", searchOffset);
        paramMap.put("limit", searchLimit);
        paramMap.put("shaIn", shaInSearchCondition);

        int count = shaInService.searchCount(shaIn);
        List<ShaInDataBase> shaInDataBases = shaInService.getShaIns(paramMap);

        shaInSearchResult.setCount(count);
        shaInSearchResult.setData(shaInDataBases);

        return shaInSearchResult;
    }

    // ページング検索
    @GetMapping("list/search/page")
    @ResponseBody
    public ShaInSearchResult shaInSearchResultPage(@RequestParam(name = "page") int pageNum) {

        Map<String, Object> paramMap = new HashMap<>();

        ShaInSearchResult shaInSearchResult = new ShaInSearchResult();

        Integer searchOffset = 10*(pageNum-1)+1;
        Integer searchLimit = 10*pageNum;

        paramMap.put("offset", searchOffset);
        paramMap.put("limit", searchLimit);
        paramMap.put("shaIn", shaInSearchCondition);

        int count = shaInService.searchCount(shaInSearchCondition);
        List<ShaInDataBase> shaInDataBases = shaInService.getShaIns(paramMap);

        shaInSearchResult.setCount(count);
        shaInSearchResult.setData(shaInDataBases);


        return shaInSearchResult;
    }


}

