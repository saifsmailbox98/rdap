package be.dnsbelgium.rdap;

import be.dnsbelgium.core.DomainName;
import be.dnsbelgium.rdap.core.*;
import be.dnsbelgium.rdap.jackson.CustomObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractControllerTest {

  protected final DateTime createTime = DateTime.now().toDateTime(DateTimeZone.UTC).minusDays(200);
  protected final DateTime lastChangedTime = createTime.plusDays(100);

  protected MockMvc mockMvc;

  @Resource
  protected WebApplicationContext webApplicationContext;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Configuration
  abstract static class Config extends WebMvcConfigurationSupport {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
      converters.add(converter());
    }

    @Bean
    MappingJacksonHttpMessageConverter converter() {
      MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
      converter.setObjectMapper(new CustomObjectMapper());
      return converter;
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
      RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
      handlerMapping.setUseSuffixPatternMatch(false);
      handlerMapping.setUseTrailingSlashMatch(false);
      return handlerMapping;
    }
  }

  protected List<Link> someLinks() throws Exception {
    Set<String> hrefLang = new HashSet<String>();
    hrefLang.add("en");
    hrefLang.add("de");
    List<String> title = new ArrayList<String>();
    title.add("Title part 1");
    title.add("Title part 2");
    List<Link> links = new ArrayList<Link>();
    links.add(new Link(new URI("http://example.com/value"), "rel", new URI("http://example.com/href"), hrefLang, title, "Media", "Type"));
    links.add(new Link(new URI("http://example.com/value"), "rel", new URI("http://example.com/href"), hrefLang, title, "Media", "Type"));
    return links;
  }

  protected List<Notice> someNotices() throws Exception {
    List<Notice> notices = new ArrayList<Notice>();
    notices.add(aNoticeOrRemark());
    notices.add(aNoticeOrRemark());
    return notices;
  }

  protected List<Notice> someRemarks() throws Exception {
    List<Notice> remarks = new ArrayList<Notice>();
    remarks.add(aNoticeOrRemark());
    remarks.add(aNoticeOrRemark());
    return remarks;
  }

  protected Notice aNoticeOrRemark() throws Exception {
    List<String> description = new ArrayList<String>();
    description.add("Description part 1");
    description.add("Description part 2");
    return new Notice("Title", "Type", description, someLinks());
  }

  protected List<Event> someEvents() throws Exception {
    List<Event> events = new ArrayList<Event>();
    events.add(new Event(Event.Action.Default.REGISTRATION, "EventActor", createTime, someLinks()));
    events.add(new Event(Event.Action.Default.LAST_CHANGED, "EventActor", lastChangedTime, someLinks()));
    return events;
  }

  protected List<Status> someStatuses() throws Exception {
    List<Status> statuses = new ArrayList<Status>();
    statuses.add(Status.Default.ACTIVE);
    statuses.add(Status.Default.DELETE_PROHIBITED);
    statuses.add(Status.Factory.of("some specific status"));
    return statuses;
  }

  protected List<Nameserver> someNameservers() throws Exception {
    List<Nameserver> nameservers = new ArrayList<Nameserver>();
    nameservers.add(new Nameserver(someLinks(), someNotices(), someRemarks(), "en", Nameserver.OBJECT_CLASS_NAME, someEvents(), someStatuses(), DomainName.of("whois.example.com"), "Handle", DomainName.of("ns.xn--exmple-jta.com"), DomainName.of("ns.exàmple.com"), someIpAddresses()));
    nameservers.add(new Nameserver(someLinks(), someNotices(), someRemarks(), "en", Nameserver.OBJECT_CLASS_NAME, someEvents(), someStatuses(), DomainName.of("whois.example.com"), "Handle", DomainName.of("ns.xn--exmple-jta.com"), DomainName.of("ns.exàmple.com"), someIpAddresses()));
    return nameservers;
  }

  protected Nameserver.IpAddresses someIpAddresses() {
    List<String> v4s = new ArrayList<String>();
    v4s.add("193.5.6.198");
    v4s.add("89.65.3.87");
    List<String> v6s = new ArrayList<String>();
    v6s.add("2001:678:9::1");
    v6s.add("FE80:0000:0000:0000:0202:B3FF:FE1E:8329");
    return new Nameserver.IpAddresses(v4s, v6s);
  }

  protected List<PublicId> somePublicIds() {
    List<PublicId> publicIds = new ArrayList<PublicId>();
    publicIds.add(new PublicId("Type", "Identifier"));
    publicIds.add(new PublicId("Type", "Identifier"));
    return publicIds;
  }
}
