package api;

/**
 * @author wanjiahao
 */
public interface HelloService {
    /**
     * 打招呼
     * @param name
     * @return
     */
    String hello(String name);

    String hello();
    String hello(int a);

    String hello(HelloPara helloPara);
}
