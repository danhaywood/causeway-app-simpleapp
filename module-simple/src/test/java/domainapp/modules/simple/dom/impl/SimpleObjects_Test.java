package domainapp.modules.simple.dom.impl;

import java.util.List;

import org.apache.isis.persistence.jdo.applib.services.IsisJdoSupport_v3_2;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.commons.internal.collections._Lists;
import org.apache.isis.unittestsupport.jmocking.JMockActions;
import org.apache.isis.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleObjects_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;
    @Mock
    IsisJdoSupport_v3_2 mockIsisJdoSupport_v3_2;

    SimpleObjects simpleObjects;

    @Before
    public void setUp() {
        simpleObjects = new SimpleObjects(mockRepositoryService, mockIsisJdoSupport_v3_2);
    }

    public static class Create extends SimpleObjects_Test {

        @Test
        public void happyCase() {

            final String someName = "Foobar";

            // given
            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).persist(with(nameOf(someName)));
                    will(JMockActions.returnArgument(0));
                }

            });

            // when
            final SimpleObject obj = simpleObjects.create(someName);

            // then
            assertThat(obj).isNotNull();
            assertThat(obj.getName()).isEqualTo(someName);
        }

        private static Matcher<SimpleObject> nameOf(final String name) {
            return new TypeSafeMatcher<SimpleObject>() {
                @Override
                protected boolean matchesSafely(final SimpleObject item) {
                    return name.equals(item.getName());
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("has name of '" + name + "'");
                }
            };
        }
    }

    public static class ListAll extends SimpleObjects_Test {

        @Test
        public void happyCase() {

            // given
            final List<SimpleObject> all = _Lists.newArrayList();

            context.checking(new Expectations() {
                {
                    oneOf(mockRepositoryService).allInstances(SimpleObject.class);
                    will(returnValue(all));
                }
            });

            // when
            final List<SimpleObject> list = simpleObjects.listAll();

            // then
            assertThat(list).isEqualTo(all);
        }
    }
}
