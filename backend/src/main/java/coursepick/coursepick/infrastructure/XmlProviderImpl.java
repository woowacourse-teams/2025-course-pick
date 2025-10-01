package coursepick.coursepick.infrastructure;

import com.ctc.wstx.shaded.msv_core.verifier.jaxp.DocumentBuilderFactoryImpl;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import io.jenetics.jpx.XMLProvider;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

public class XmlProviderImpl extends XMLProvider {

    @Override
    public XMLInputFactory xmlInputFactory() {
        return new WstxInputFactory();
    }

    @Override
    public XMLOutputFactory xmlOutputFactory() {
        return new WstxOutputFactory();
    }

    @Override
    public DocumentBuilderFactory documentBuilderFactory() {
        return new DocumentBuilderFactoryImpl();
    }
}
