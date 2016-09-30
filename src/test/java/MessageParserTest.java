import de.email.aux.MessageParser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class MessageParserTest {

    @Test
    public void parseEmailAddress() {
        String ea = "  <    h e l l o . w o r l d @ j a v a . c o m >";
        String actual = MessageParser.parseEmailAddress(ea);
        String expected = "    h e l l o . w o r l d @ j a v a . c o m ";
        Assert.assertEquals(actual, expected);
        ea = "<this>";
        actual = MessageParser.parseEmailAddress(ea);
        expected = "this";
        Assert.assertEquals(actual, expected);
        ea = "fsdljkfdslfj dlkfj dslkfj ";
        actual = MessageParser.parseEmailAddress(ea);
        expected = "fsdljkfdslfj dlkfj dslkfj ";
        Assert.assertEquals(actual, expected);
    }
}
