//package com.github.darylyeung.zentaoplugin.extension.zentao;
//
//import com.github.darylyeung.zentaoplugin.ZentaoBundle;
//import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoProduct;
//import com.intellij.openapi.progress.ProgressIndicator;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.ui.ComboBox;
//import com.intellij.tasks.config.BaseRepositoryEditor;
//import com.intellij.tasks.impl.TaskUiUtil;
//import com.intellij.util.Consumer;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//
//import static com.github.darylyeung.zentaoplugin.extension.zentao.ZentaoRepository.UNSPECIFIED_PRODUCT;
//
///**
// * @author Yeung
// * @version v1.0
// * @date 2024-01-11 22:13:09
// */
//public class ZentaoRepositoryEditor extends BaseRepositoryEditor<ZentaoRepository> {
//
//    private ComboBox<ZentaoProduct> myProductComboBox;
//
//    public ZentaoRepositoryEditor(Project project, ZentaoRepository repository, Consumer<? super ZentaoRepository> changeListener) {
//        super(project, repository, changeListener);
//    }
//
//
//    @Override
//    public void apply() {
//        super.apply();
//    }
//
//    private final class FetchProjectsTask extends TaskUiUtil.ComboBoxUpdater<ZentaoProduct> {
//        private FetchProjectsTask() {
//            super(ZentaoRepositoryEditor.this.myProject, ZentaoBundle.message("progress.title.downloading.zentao.products"), myProductComboBox);
//        }
//
//        @Override
//        public ZentaoProduct getExtraItem() {
//            return UNSPECIFIED_PRODUCT;
//        }
//
//        @Nullable
//        @Override
//        public ZentaoProduct getSelectedItem() {
//            return myRepository.getCurrentProduct();
//        }
//
//        @NotNull
//        @Override
//        protected List<ZentaoProduct> fetch(@NotNull ProgressIndicator indicator) throws Exception {
//            return myRepository.fetchProducts();
//        }
//    }
//}
