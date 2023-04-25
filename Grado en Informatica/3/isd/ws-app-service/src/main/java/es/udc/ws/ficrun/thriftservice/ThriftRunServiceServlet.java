package es.udc.ws.ficrun.thriftservice;

import es.udc.ws.ficrun.thrift.ThriftRunService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;

public class ThriftRunServiceServlet extends TServlet{

    public ThriftRunServiceServlet() {
        super(createProcessor(), createProtocolFactory());
    }

    private static TProcessor createProcessor() {
        return new ThriftRunService.Processor<ThriftRunService.Iface>(new ThriftRunServiceImpl());
    }

    private static TProtocolFactory createProtocolFactory() {
        return new TBinaryProtocol.Factory();
    }
}