package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.github.darylyeung.zentaoplugin.common.ZentaoConstant;
import com.github.darylyeung.zentaoplugin.extension.zentao.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl;
import com.intellij.tasks.impl.httpclient.TaskResponseUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.darylyeung.zentaoplugin.util.ZentaoUtil.GSON;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-10 23:02:18
 */
@Tag("Zentao")
public class ZentaoRepository extends NewBaseRepositoryImpl {

    private String token;
    private ZentaoProduct myCurrentProduct;

    private List<ZentaoProduct> myProducts = null;
    //    private static final Gson GSON = TaskGsonUtil.createDefaultBuilder().create();

    private static final TypeToken<List<ZentaoProduct>> LIST_OF_PRODUCTS_TYPE = new TypeToken<>() {};

    private static final TypeToken<List<ZentaoBug>> LIST_OF_BUGS_TYPE = new TypeToken<>() {};

    public static final ZentaoProduct UNSPECIFIED_PRODUCT = createUnspecifiedProduct();

    private static ZentaoProduct createUnspecifiedProduct() {
        final ZentaoProduct unspecified = new ZentaoProduct() {
            @Override
            public String getName() {
                return "-- all issues created by you --";
            }
        };
        unspecified.setId(-1);
        return unspecified;
    }

    public ZentaoRepository() {
        super();
    }

    public ZentaoRepository(ZentaoRepository other) {
        super(other);
        myCurrentProduct = other.myCurrentProduct;
    }


    @Override
    public @Nullable Task findTask(@NotNull String s) throws Exception {
        return null;
    }

    public ZentaoRepository(TaskRepositoryType type) {
        super(type);
    }

    @Override
    public @NotNull BaseRepository clone() {
        return new ZentaoRepository(this);
    }

    @Override
    public Task[] getIssues(@Nullable String query, int offset, int limit, boolean withClosed) throws Exception {
        final List<ZentaoBug> bugs = fetchBugs((offset / limit) + 1, limit, !withClosed);
        return ContainerUtil.map2Array(bugs, ZentaoTask.class, issue -> new ZentaoTask(this, issue));
    }

    private List<ZentaoBug> fetchBugs(int pageNumber, int pageSize, boolean openedOnly) throws Exception {
        ensureProjectsDiscovered();
        final URIBuilder uriBuilder = new URIBuilder(getBugsUrl())
                .addParameter("page", String.valueOf(pageNumber))
                .addParameter("per_page", String.valueOf(pageSize))
                // Ordering was added in v7.8
                .addParameter("order_by", "updated_at");
        if (openedOnly) {
            // Filtering by state was added in v7.3
            uriBuilder.addParameter("state", "opened");
        }
        final ResponseHandler<ZentaoBugPage> handler = new TaskResponseUtil.GsonSingleObjectDeserializer<>(new Gson(), ZentaoBugPage.class);
        ZentaoBugPage page = getHttpClient().execute(new HttpGet(uriBuilder.build()), handler);
        if (CollectionUtils.isEmpty(page.getBugs())) {
            return Collections.emptyList();
        }
        //  filter my bugs
        return page.getBugs().stream().filter(i -> Objects.equals(i.getAssignedTo().getAccount(), myUsername)).collect(Collectors.toList());
    }

    private String getBugsUrl() {
        if (myCurrentProduct != null && myCurrentProduct != UNSPECIFIED_PRODUCT) {
            return getRestApiUrl("projects", myCurrentProduct.getId(), "issues");
        }
        if (myCurrentProduct == null) {
            int id = myProducts.get(0).getId();
            return getRestApiUrl("products", id, "bugs");
        }
        return getRestApiUrl("bugs");
    }

    @NotNull
    public List<ZentaoProduct> getProducts() {
        try {
            ensureProjectsDiscovered();
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(myProducts);
    }

    private void ensureProjectsDiscovered() throws Exception {
        if (myProducts == null) {
            fetchProducts();
        }
    }

    @Override
    public @NotNull String getRestApiPathPrefix() {
        return "/api.php/v1";
    }

    @Nullable
    @Override
    protected HttpRequestInterceptor createRequestInterceptor() {
        if (getToken() == null) {
            return null;
        }
        return (request, context) -> request.addHeader(ZentaoConstant.TOKEN.getCode(), token);
    }

    /**
     * Always forcibly attempt to fetch new projects from server.
     */
    @NotNull
    public List<ZentaoProduct> fetchProducts() throws Exception {
        //  设置Token
        generateToken();
        //  获取产品
        final ResponseHandler<ZentaoProductPage> handler = new TaskResponseUtil.GsonSingleObjectDeserializer<>(GSON, ZentaoProductPage.class);
        final String productUrl = getRestApiUrl("products");
        final URIBuilder productsUrl = new URIBuilder(productUrl);
        final ZentaoProductPage page = getHttpClient().execute(new HttpGet(productsUrl.build()), handler);
        if (Objects.isNull(page) || page.getTotal() < 1) {
            return Collections.emptyList();
        }
        myProducts = page.getProducts();
        return Collections.unmodifiableList(myProducts);
    }

    private void generateToken() throws URISyntaxException, IOException {
        final ResponseHandler<ZentaoToken> handler = new TaskResponseUtil.GsonSingleObjectDeserializer<>(GSON, ZentaoToken.class);
        String tokenUrl = getRestApiUrl("tokens");
        final URIBuilder uriBuilder = new URIBuilder(tokenUrl);
        HttpPost request = new HttpPost(uriBuilder.build());
        request.setEntity(new StringEntity(GSON.toJson(new ZentaoLogin(myUsername, myPassword)), ContentType.APPLICATION_JSON));
        final ZentaoToken token = getHttpClient().execute(request, handler);
        if (token != null) {
            setToken(token.getToken());
        }
    }

    public ZentaoProduct getCurrentProduct() {
        return myCurrentProduct;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
