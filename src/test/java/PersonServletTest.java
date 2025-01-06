import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.thesquad.connection.DBConnection;
import com.thesquad.controllers.PersonServlet;
import com.thesquad.dao.AddressDAO;
import com.thesquad.dao.PersonDAO;
import com.thesquad.dao.ProvinceDAO;
import com.thesquad.models.AddressModel;
import com.thesquad.models.PersonModel;
import com.thesquad.models.ProvinceModel;
import com.thesquad.utils.Helpers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class PersonServletTest {
        private PersonServlet personServlet;
        private HttpServletRequest request;
        private HttpServletResponse response;
        private DBConnection connection;

        @BeforeEach
        public void setup() {
            personServlet = new PersonServlet();
            request = mock(HttpServletRequest.class);
            response = mock(HttpServletResponse.class);
            connection = mock(DBConnection.class);
        }

        @Test
        void testDoGet_provinceOperation() throws Exception {
            when(request.getParameter("operation")).thenReturn("province");
            when(request.getParameter("id")).thenReturn("1");

            ProvinceDAO provinceDao = mock(ProvinceDAO.class);
            List<ProvinceModel> provinces = new ArrayList<>();
            ProvinceModel province = new ProvinceModel();
            province.setProvinceId(1);
            province.setName("Test Province");
            provinces.add(province);

            when(provinceDao.getProvincesByCountryId(1, connection)).thenReturn(provinces);

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            when(response.getWriter()).thenReturn(printWriter);

            personServlet.service(request, response);

            printWriter.flush();
            Gson gson = new Gson();
            assertEquals(gson.toJson(provinces), stringWriter.toString().trim());
        }

        @Test
        void testDoGet_deleteAction() throws Exception {
            when(request.getParameter("action")).thenReturn("delete");
            when(request.getParameter("id")).thenReturn("1");

            PersonDAO personDao = mock(PersonDAO.class);

            personServlet.service(request, response);

            verify(personDao, times(1)).delete(1, connection);
            verify(response).sendRedirect(anyString());
        }

        @Test
        void testDoPost_createPerson() throws Exception {
            when(request.getParameter("name")).thenReturn("John");
            when(request.getParameter("surname")).thenReturn("Doe");
            when(request.getParameter("bi")).thenReturn("12345");
            when(request.getParameter("birthDate")).thenReturn("2000-01-01");
            when(request.getParameter("gender")).thenReturn("1");
            when(request.getParameter("street")).thenReturn("Main Street");
            when(request.getParameter("houseNumber")).thenReturn("10");
            when(request.getParameter("neighborhood")).thenReturn("Downtown");
            when(request.getParameter("districtId")).thenReturn("2");
            when(request.getParameter("phone1")).thenReturn("555123456");
            when(request.getParameter("phone2")).thenReturn("");
            when(request.getParameter("email1")).thenReturn("john.doe@example.com");
            when(request.getParameter("email2")).thenReturn("");
            when(request.getParameter("personType")).thenReturn("READER");

            PersonDAO personDao = mock(PersonDAO.class);
            AddressDAO addressDao = mock(AddressDAO.class);

            Helpers helpers = mock(Helpers.class);
            when(Helpers.getIdOfLastRow(anyString(), eq(connection))).thenReturn(1);

            personServlet.service(request, response);

            ArgumentCaptor<AddressModel> addressCaptor = ArgumentCaptor.forClass(AddressModel.class);
            verify(addressDao).create(addressCaptor.capture(), eq(connection));
            assertEquals("Main Street", addressCaptor.getValue().getStreet());

            ArgumentCaptor<PersonModel> personCaptor = ArgumentCaptor.forClass(PersonModel.class);
            verify(personDao).create(personCaptor.capture(), eq(connection));
            assertEquals("John", personCaptor.getValue().getName());

            verify(response).sendRedirect(anyString());
        }
}