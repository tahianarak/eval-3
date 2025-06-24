package eval.newApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class MysqlService
{
    @Autowired
    DataSource dataSource;

    public String showConnection()throws Exception
    {
        try(Connection connection=dataSource.getConnection())
        {
            String sql="select * from tabCompany History";
            try(PreparedStatement preparedStatement=connection.prepareStatement(sql))
            {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    String ans="";
                    while (rs.next()) {
                        ans=ans+rs.getString("name")+"\n";
                    }
                    return ans;
                }
            }

        }
    }

}
