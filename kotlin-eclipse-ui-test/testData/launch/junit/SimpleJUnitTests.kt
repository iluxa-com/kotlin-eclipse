import org.junit.Test
import org.junit.Assert

public class SomeKt {
	Test fun okTest() {
		Assert.assertEquals(0, 0)
	}
	
	Test fun failTest() {
		Assert.assertEquals(0, 1)
	}
}